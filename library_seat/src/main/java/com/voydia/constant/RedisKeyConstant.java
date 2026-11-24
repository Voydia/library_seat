package com.voydia.constant;

/**
 * Redis Key 前缀
 */
public final class RedisKeyConstant {

    /** 登录 token 前缀，完整 key 为 jwt:login:{userId} */
    public static final String LOGIN_TOKEN = "jwt:login:";

    private RedisKeyConstant() {
    }
}
