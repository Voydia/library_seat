package com.voydia.entity;


import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;


import java.time.LocalDateTime;

@Data //lombok注解，自动生成getter和setter方法
@TableName("user")
public class User {


    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;
    private String username;
    private String password;

    @TableField("create_time")
    private LocalDateTime createTime;
}
