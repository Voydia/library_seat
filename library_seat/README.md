# 图书馆座位预约系统

基于 Spring Boot 的图书馆座位预约后端，支持用户注册登录、座位查询、预约、签到、取消，以及「超时未签到自动释放座位」的延迟消息机制。

前端工程：[library-seat-web](../library-seat-web)（Vue 3 + Vite，独立仓库）

## 技术栈

| 分类 | 技术 |
| --- | --- |
| 框架 | Spring Boot 4.0.6 / Java 17 |
| 持久层 | MyBatis-Plus 3.5.15 + MySQL 8 |
| 缓存 | Redis（Lettuce 连接池）+ Redisson 3.45.1 |
| 消息队列 | RabbitMQ 4.4（quorum 队列原生延迟消息） |
| 认证 | JWT（jjwt 0.11.5）+ Redis 令牌校验 |
| 密码加密 | BCrypt（spring-security-crypto） |
| 接口文档 | springdoc-openapi 3.2.0（Swagger UI） |
| 工具 | Hutool、Lombok |

## 功能特性

- **用户模块**：手机号注册、登录、退出登录
- **座位模块**：座位列表、按 id 查询、增删改
- **预约模块**：创建预约、签到、取消预约、查看我的预约
- **并发控制**：Redisson 分布式锁，防止高峰期同一座位被重复预约
- **超时释放**：预约成功后投递延迟消息，超过宽限期未签到自动释放座位
- **登录鉴权**：JWT 无状态令牌 + Redis 存储，支持多实例部署与主动下线
- **公共字段自动填充**：基于 ThreadLocal 获取当前用户，自动填充创建人/更新人/时间
- **统一响应**：`Result` 统一返回结构，配套全局异常处理

## 项目结构

```
src/main/java/com/voydia
├── common/          通用组件
├── config/          配置类（CORS、拦截器、Redisson、RabbitMQ、JWT）
├── constant/        状态与 Redis Key 常量
├── controller/      接口层
├── dto/             数据传输对象（Result、LoginFormDTO 等）
├── entity/          数据库实体
├── handler/         全局异常、MyBatis-Plus 字段自动填充
├── mapper/          MyBatis-Plus Mapper
├── mq/              延迟消息生产者与消费者
├── service/         业务接口
│   └── impl/        业务实现
└── util/            JWT、ThreadLocal、正则校验工具
```

## 快速开始

### 1. 环境要求

- JDK 17+
- Maven 3.8+
- MySQL 8.0+
- Redis 6.0+
- RabbitMQ 4.4+（低于 4.4 需额外安装延迟消息插件）

### 2. 初始化数据库

```sql
CREATE DATABASE library_seat DEFAULT CHARACTER SET utf8mb4;
```

数据表 `user`、`seat`、`reservation`，字段与 `com.voydia.entity` 下的实体保持一致，公共字段包含 `create_time`、`update_time`、`create_user`、`update_user`。

### 3. 修改配置

编辑 `src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/library_seat?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: 你的数据库密码
  data:
    redis:
      host: 127.0.0.1
      port: 6379
      password: 你的 Redis 密码
  rabbitmq:
    host: 127.0.0.1
    port: 5672
    username: guest
    password: guest
jwt:
  secret: 至少 32 字节的随机字符串
  expiration: 7200000   # 毫秒
```

> 生产环境建议改用环境变量占位符，例如 `${REDIS_PASSWORD:}`，避免把密码提交到仓库。

### 4. 启动

```bash
mvn spring-boot:run
```

服务端口：`8081`

- Swagger 文档：http://localhost:8081/swagger-ui/index.html
- OpenAPI JSON：http://localhost:8081/v3/api-docs

## 接口说明

除登录、注册外，其余接口需在请求头携带令牌：

```
Authorization: Bearer <token>
```

| 模块 | 方法 | 路径 | 说明 |
| --- | --- | --- | --- |
| 用户 | POST | `/user/register` | 注册 |
| 用户 | POST | `/user/login` | 登录，返回 JWT |
| 座位 | GET | `/seat/all` | 全部座位 |
| 座位 | GET | `/seat/{id}` | 按 id 查询 |
| 座位 | POST | `/seat` | 新增座位 |
| 座位 | PUT | `/seat` | 更新座位 |
| 座位 | DELETE | `/seat/{id}` | 删除座位 |
| 预约 | POST | `/reservation` | 创建预约 |
| 预约 | GET | `/reservation/my` | 我的预约 |
| 预约 | PUT | `/reservation/checkin/{id}` | 签到 |
| 预约 | DELETE | `/reservation/{id}` | 取消预约 |

统一响应格式：

```json
{
  "success": true,
  "errorMsg": null,
  "data": {},
  "total": null
}
```

## 关键设计

### 登录鉴权（JWT + Redis）

登录成功后生成 JWT 并写入 Redis（`jwt:login:{userId}`），拦截器校验签名后再比对 Redis 中的令牌，实现：

- 服务端可主动失效（退出登录、踢下线）
- 多实例共享登录状态，不依赖 Session
- 新登录自动顶掉旧令牌

### 防超卖（Redisson 分布式锁）

预约按座位粒度加锁（`lock:reserve:seat:{seatId}`），把「查冲突 + 写预约」变成互斥操作。锁在事务外层，保证提交后才释放，避免锁释放时数据尚未落库导致的重复预约。

### 超时释放（RabbitMQ 延迟消息）

预约成功后投递一条延迟消息，延迟时长 = 预约开始时间 + 宽限期（15 分钟） - 当前时间。消费端做幂等判断：

- 已签到 / 已取消 → 跳过
- 仍为待签到 → 置为超时释放，并恢复座位状态

RabbitMQ 4.4 起使用 `x-opt-delivery-time`（绝对 Unix 毫秒时间戳）声明投递时间，无需额外插件。

### 公共字段自动填充

登录拦截器把用户 id 写入 ThreadLocal，`MetaObjectHandler` 在插入/更新时读取并填充 `create_user`、`update_user` 和对应时间字段，请求结束后清理 ThreadLocal 防止线程复用串号。

## 待完善

- [ ] 用户角色（管理员/普通用户）与接口权限划分
- [ ] 退出登录接口补充路由映射
- [ ] 预约记录分页查询
- [ ] 单元测试与并发压测脚本
