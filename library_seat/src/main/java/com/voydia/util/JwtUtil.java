package com.voydia.util;

import com.voydia.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    @Autowired
    private JwtProperties jwtProperties;

    // 从配置文件读取密钥，而不是硬编码
    private SecretKey getSecretKey() {
        // 确保密钥足够长（至少256位）
        return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes());
    }
    /**
     * 生成 JWT Token
     */
    public String createToken(Integer userId, String username) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId)) // 主体：用户ID
                .claim("username", username)        // 自定义载荷：用户名
                .setIssuedAt(new Date())            // 签发时间
                .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getExpiration())) // 过期时间
                .signWith(getSecretKey())               // 签名
                .compact();
    }

    /**
     * 解析 JWT Token
     */
    public Claims parseToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSecretKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            return null; // 解析失败（过期、篡改等）
        }
    }
}
