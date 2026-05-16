package com.voydia.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.voydia.mapper.UserMapper;
import com.voydia.entity.User;
import org.springframework.stereotype.Service;

@Service
//用户服务
public class UserService extends ServiceImpl<UserMapper, User> {
    //根据用户名查询用户
    public User getUserByUsername(String username) {
        return this.lambdaQuery()
                .eq(User::getUsername, username)//eq表示等于
                .one();//返回一个用户
    }
}
