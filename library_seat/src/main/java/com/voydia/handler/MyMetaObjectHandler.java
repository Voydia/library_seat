package com.voydia.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.voydia.util.UserHolder;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        // 插入时自动填充的字段
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());

        // 获取当前登录用户ID并填充
        Integer currentUserId = UserHolder.getCurrentUserId();
        if (currentUserId != null) {
            this.strictInsertFill(metaObject, "createUser", Integer.class, currentUserId);
            this.strictInsertFill(metaObject, "updateUser", Integer.class, currentUserId);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        // 更新时自动填充的字段
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());

        // 获取当前登录用户ID并填充
        Integer currentUserId = UserHolder.getCurrentUserId();
        if (currentUserId != null) {
            this.strictUpdateFill(metaObject, "updateUser", Integer.class, currentUserId);
        }
    }
}
