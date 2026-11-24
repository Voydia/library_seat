package com.voydia.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.voydia.entity.Seat;
import com.voydia.mapper.SeatMapper;
import com.voydia.service.ISeatService;
import org.springframework.stereotype.Service;

@Service
public class SeatServiceImpl extends ServiceImpl<SeatMapper, Seat> implements ISeatService {
}
