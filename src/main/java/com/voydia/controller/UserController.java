package com.voydia.controller;


import com.voydia.service.UserService;
import com.voydia.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@Tag(name="用户管理",description="用户相关接口")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(summary = "获取所有用户",description = "获取所有用户信息")
    public List<User> getAllUsers(){
        return userService.list();
    }

    @Operation(summary="根据id查询用户")
    @GetMapping("/{id}")
    public User getUserById(@PathVariable Integer id){
        return userService.getById(id);
    }

    @PostMapping
    public boolean addUser(@RequestBody User user){
        return userService.save(user);
    }

    @DeleteMapping
    public boolean deleteUser(@PathVariable Integer id){
        return userService.removeById(id);
    }

    @PutMapping
    public boolean updateUser(@RequestBody User user){
        return userService.updateById(user);
    }

}
