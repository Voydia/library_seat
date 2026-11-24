package com.voydia.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.voydia.config.JwtProperties;
import com.voydia.constant.RedisKeyConstant;
import com.voydia.dto.LoginFormDTO;
import com.voydia.dto.Result;
import com.voydia.entity.User;
import com.voydia.mapper.UserMapper;
import com.voydia.service.IUserService;
import com.voydia.util.JwtUtil;
import com.voydia.util.RegexUtils;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;


@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private JwtUtil jwtUtil;

    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();
    @Autowired
    private JwtProperties jwtProperties;
    //login
    @Override
    public Result login(LoginFormDTO loginForm) {
        //检验手机号
        String phone = loginForm.getPhone();
        if(RegexUtils.isPhoneInvalid(phone)){
            return Result.fail("手机号格式错误");
        }
        //根据手机号查用户
        User user = query().eq("phone",phone).one();
        //判断用户是否存在
        if(user ==null){
            //不存在注册
            return Result.fail("用户未注册");
        }
        //存在,校验密码
        String rawPassword = loginForm.getPassword();
        if(!ENCODER.matches(loginForm.getPassword(), user.getPassword())) {
            return Result.fail("密码错误");
        }
        //生成JWTtoken
        String token = jwtUtil.createToken(user.getId(),user.getUsername());
        //存入redis(解决分布式session共享的核心问题
        //Key:iwt:login:{userId}
        //Value:token
        String redisKey = RedisKeyConstant.LOGIN_TOKEN + user.getId() ;

        //设置与JWT相同的过期时间
        stringRedisTemplate.opsForValue().set(redisKey,token,jwtProperties.getExpiration(), TimeUnit.MILLISECONDS);

        return Result.ok(token);
    }

    @Override
    public Result register(LoginFormDTO loginForm) {
        //检验手机号
        String phone = loginForm.getPhone();
        if(RegexUtils.isPhoneInvalid(phone)){
            return Result.fail("手机号格式错误");
        }
        if (RegexUtils.isUsernameInvalid(loginForm.getUsername())) {
            return Result.fail("用户名需为 4-16 位字母、数字或下划线");
        }
        //根据手机号查用户
        User user = query().eq("phone",phone).one();
        //判断用户是否存在
        if(user !=null){
            //存在,返回错误信息
            return Result.fail("用户已存在");
        }
        //不存在,注册
        user = new User();
        user.setPhone(phone);
        user.setUsername(loginForm.getUsername());
        user.setPassword(ENCODER.encode(loginForm.getPassword()));
        save(user);
        return Result.ok("注册成功,请前往登录");
    }

    @Override
    public Result logout(String token) {
        Claims claims = jwtUtil.parseToken(token);
        if (claims != null) {
            stringRedisTemplate.delete(RedisKeyConstant.LOGIN_TOKEN + claims.getSubject());
        }
        return Result.ok("已退出登录");
    }
}
