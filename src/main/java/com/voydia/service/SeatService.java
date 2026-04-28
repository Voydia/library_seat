package com.voydia.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.voydia.entity.Seat;
import com.voydia.mapper.SeatMapper;
import org.springframework.stereotype.Service;

@Service//座位服务
public class SeatService extends ServiceImpl<SeatMapper, Seat> {
}
