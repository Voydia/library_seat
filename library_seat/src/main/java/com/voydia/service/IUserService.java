package com.voydia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.voydia.dto.LoginFormDTO;
import com.voydia.dto.Result;
import com.voydia.mapper.UserMapper;
import com.voydia.entity.User;
import org.springframework.stereotype.Service;

//用户服务
public interface IUserService extends IService<User> {
    //登录

    Result login(LoginFormDTO loginForm);

    Result register(LoginFormDTO loginForm);

    Result logout(String substring);
}
