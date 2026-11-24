package com.voydia.service;

import com.voydia.dto.Result;
import com.voydia.entity.Reservation;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 应用层：负责加锁 + 调用事务层
 * 不参与 Spring 事务，只管并发控制
 */
@Service
public class ReservationApplicationService {

    @Autowired
    private IReservationService reservationService;

    @Autowired
    private RedissonClient redissonClient;

    public Result createReservation(Reservation reservation) {
        RLock lock = redissonClient.getLock("lock:reserve:seat:" + reservation.getSeatId());
        boolean acquired = false;
        try {
            acquired = lock.tryLock(2, 5, TimeUnit.SECONDS);
            if (!acquired) return Result.fail("当前座位预约人数较多，请重试");
            return reservationService.createWithLock(reservation); // ← 接口新方法
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return Result.fail("获取锁时被中断,请重试");
        } finally {
            if (acquired && lock.isHeldByCurrentThread()) lock.unlock();
        }
    }
}