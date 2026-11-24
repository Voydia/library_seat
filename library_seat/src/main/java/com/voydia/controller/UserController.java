package com.voydia.controller;


import com.voydia.dto.LoginFormDTO;
import com.voydia.dto.Result;
import com.voydia.service.IUserService;
import com.voydia.entity.User;
import com.voydia.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@Tag(name="用户管理",description="用户相关接口")
public class UserController {

    @Autowired
    private IUserService userService;

    /**
     * 登录
     */
    @PostMapping("/login")
    @Operation(summary = "登录")
    public Result login(@RequestBody LoginFormDTO loginForm){
        return userService.login(loginForm);
    }

    @PostMapping("/register")
    @Operation(summary = "注册")
    public Result register(@RequestBody LoginFormDTO loginForm){
        return userService.register(loginForm);
    }

    public Result logout(@RequestHeader(value = "Authorization",required = false) String authHeader){
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Result.fail("未登录");
        }
        return userService.logout(authHeader.substring(7));
    }

}
