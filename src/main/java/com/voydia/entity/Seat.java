package com.voydia.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("seat")
public class Seat {
   @TableId(value="id",type = IdType.AUTO)
    private Integer id;

    @TableField("seat_no")
    private String seatNo;

    private Integer status;

    private Integer floor;

    @TableField("create_time")
    private LocalDateTime createTime;
}
