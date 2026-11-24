package com.voydia.config;

import cn.hutool.json.JSONUtil;
import com.voydia.constant.RedisKeyConstant;
import com.voydia.dto.Result;
import com.voydia.util.JwtUtil;
import com.voydia.util.UserHolder;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * JWT拦截器
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private JwtProperties jwtProperties;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 跨域预检请求直接放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 1. 获取 token
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return unauthorized(response, "未携带 token");
        }
        String token = authHeader.substring(7);

        // 2. 校验 JWT 签名与格式
        Claims claims = jwtUtil.parseToken(token);
        if (claims == null) {
            return unauthorized(response, "token 无效或已过期");
        }

        // 3. 校验 Redis（分布式状态共享与主动失效）
        String userId = claims.getSubject();
        String redisKey = RedisKeyConstant.LOGIN_TOKEN + userId;
        String savedToken = stringRedisTemplate.opsForValue().get(redisKey);
        if (!token.equals(savedToken)) {
            return unauthorized(response, "登录已失效，请重新登录");
        }

        // 4. 滑动续签
        stringRedisTemplate.expire(redisKey, jwtProperties.getExpiration(), TimeUnit.MILLISECONDS);

        // 5. 用户信息存入 ThreadLocal
        UserHolder.setCurrentUserId(Integer.valueOf(userId));

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserHolder.clear();
    }

    /**
     * 统一返回 401 + JSON
     */
    private boolean unauthorized(HttpServletResponse response, String msg) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(JSONUtil.toJsonStr(Result.fail(msg)));
        return false;
    }
}