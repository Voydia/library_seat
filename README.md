# 图书馆座位预约系统

前后端分离的图书馆座位预约系统。用户注册登录后可查询座位、预约、签到；超过宽限期仍未签到时，系统通过延迟消息自动释放座位，供他人预约。

## 仓库结构

```
.
├── library_seat/      后端：Spring Boot 4 + MyBatis-Plus + MySQL + Redis + RabbitMQ
└── library-seat-web/  前端：Vue 3 + Vite
```

后端详细说明（接口清单、设计思路、配置项）见 [library_seat/README.md](library_seat/README.md)。

## 功能

- 用户注册、登录、退出登录（JWT + Redis 无状态令牌）
- 座位查询与管理
- 座位预约、签到、取消、我的预约
- 高峰期防超卖：Redisson 分布式锁，按座位粒度串行化
- 超时未签到自动释放：RabbitMQ 延迟消息 + 幂等消费
- 公共字段自动填充：ThreadLocal 取当前用户 + MyBatis-Plus MetaObjectHandler
- 统一响应结构与全局异常处理
- Swagger 在线接口文档

## 技术栈

**后端**

| 分类 | 技术 |
| --- | --- |
| 框架 | Spring Boot 4.0.6 / Java 17 |
| 持久层 | MyBatis-Plus 3.5.15 + MySQL 8 |
| 缓存 | Redis + Redisson 3.45.1 |
| 消息队列 | RabbitMQ 4.4（quorum 队列原生延迟消息） |
| 认证 | JWT（jjwt 0.11.5）+ BCrypt |
| 文档 | springdoc-openapi 3.2.0 |

**前端**

| 分类 | 技术 |
| --- | --- |
| 框架 | Vue 3 |
| 构建 | Vite |
| 请求 | Axios |

## 快速开始

### 1. 启动依赖服务

需要本地或远程可用的 MySQL 8、Redis、RabbitMQ 4.4+。

### 2. 启动后端

```bash
cd library_seat
# 按需修改 src/main/resources/application.yml 中的数据库、Redis、RabbitMQ 连接信息
mvn spring-boot:run
```

- 服务端口：`8081`
- Swagger：http://localhost:8081/swagger-ui/index.html

> RabbitMQ 低于 4.4 时不支持原生延迟消息，需要改用 TTL + 死信队列方案。

### 3. 启动前端

```bash
cd library-seat-web
npm install
npm run dev
```

前端默认运行在 `5173`，开发环境通过 Vite 代理把 `/api` 转发到后端，避免跨域。

## 接口约定

除登录、注册外，所有接口需在请求头携带令牌：

```
Authorization: Bearer <token>
```

统一响应：

```json
{
  "success": true,
  "errorMsg": null,
  "data": {},
  "total": null
}
```

## 说明

- `application.yml` 中的密码为本地开发配置，部署时请改为环境变量或私有配置，避免泄露
- 前后端共用同一套业务语义，接口返回的座位状态、预约状态见后端 `constant` 包
