package com.voydia.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


import java.time.LocalDateTime;


@Data
@TableName("reservation")
public class Reservation {

    @TableId
    private Integer id;

    @TableField("seat_id")
    private Integer seatId;

    @TableField("user_id")
    private Integer userId;

    @TableField("start_time")
    private LocalDateTime startTime;
    @TableField("end_time")
    private LocalDateTime endTime;

    private int status;
    @TableField("create_time")
    private LocalDateTime createTime;
}
