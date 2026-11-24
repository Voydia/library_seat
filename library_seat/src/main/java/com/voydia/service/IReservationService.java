package com.voydia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.voydia.dto.Result;
import com.voydia.entity.Reservation;

import java.time.LocalDateTime;

//预约服务
public interface IReservationService extends IService<Reservation> {

    //签到
    Result checkin(Integer id) ;



    //创建预约
    Result createWithLock(Reservation reservation);

    //检查座位在指定时间段是否可用
    //Result isSeatAvaliable(Integer seatId, LocalDateTime startTime, LocalDateTime endTime);

    //取消预约
    Result cancelReservation(Integer id);

    //我的预约列表
    Result listMyReservations();
}
