package com.voydia.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.voydia.constant.ReservationStatus;
import com.voydia.constant.SeatStatus;
import com.voydia.dto.Result;
import com.voydia.entity.Reservation;
import com.voydia.entity.Seat;
import com.voydia.mapper.ReservationMapper;
import com.voydia.mq.ReservationDelayProducer;
import com.voydia.service.IReservationService;
import com.voydia.service.ISeatService;
import com.voydia.util.UserHolder;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservationServiceImpl extends ServiceImpl<ReservationMapper, Reservation> implements IReservationService {

    @Autowired
    private ISeatService seatService;

    @Autowired
    private ReservationDelayProducer producer;

    @Autowired
    private RedissonClient redissonClient;

    private static final int GRACE_MINUTES = 15; // 宽限期：开始后 15 分钟内可签到

    @Transactional
    public void releaseIfNotCheckedIn(Integer reservationId) {
        Reservation r = getById(reservationId);
        if (r == null || r.getStatus() != ReservationStatus.PENDING) {
            return; // 已删除 / 已签到 / 已取消，全部跳过
        }

        // 兜底：还没到「开始时间 + 宽限期」，说明消息提前到了，重新投递
        LocalDateTime deadline = r.getStartTime().plusMinutes(GRACE_MINUTES);
        long remain = Duration.between(LocalDateTime.now(), deadline).toMillis();
        if (remain > 0) {
            producer.sendTimeoutCheck(reservationId, remain);
            return;
        }

        r.setStatus(ReservationStatus.RELEASED);
        updateById(r);

        Seat seat = seatService.getById(r.getSeatId());
        if (seat != null) {
            seat.setStatus(SeatStatus.FREE);
            seatService.updateById(seat);
        }
    }

    /**
     * 签到
     * @param id
     * @return
     */
    @Override
    @Transactional
    public Result checkin(Integer id) {
        Reservation r = getById(id);
        if (r == null) {
            return Result.fail("预约不存在");
        }
        if (!r.getUserId().equals(UserHolder.getCurrentUserId())) {
            return Result.fail("无权操作他人预约");
        }
        if (r.getStatus() != ReservationStatus.PENDING) {
            return Result.fail("当前状态不可签到");
        }
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(r.getStartTime())) {
            return Result.fail("还未到签到时间");
        }
        if (now.isAfter(r.getStartTime().plusMinutes(GRACE_MINUTES))) {
            return Result.fail("已超过签到时间");
        }
        r.setStatus(ReservationStatus.CHECKED_IN);
        updateById(r);
        return Result.ok("签到成功");
    }

    /**
     * 预约
     * @param reservation
     * @return
     */
    @Override
    @Transactional
    public Result createWithLock(Reservation reservation) {
        if (!isSeatAvaliable(reservation.getSeatId(),
                reservation.getStartTime(), reservation.getEndTime())) {
            return Result.fail("座位已被预约");
        }
        reservation.setStatus(ReservationStatus.PENDING);
        if (!save(reservation)) {
            return Result.fail("预约失败");
        }

        Seat seat = seatService.getById(reservation.getSeatId());
        if (seat != null) {
            seat.setStatus(SeatStatus.OCCUPIED);
            seatService.updateById(seat);
        } else {
            return Result.fail("座位不存在");
        }

        long delay = Duration.between(LocalDateTime.now(),
                reservation.getStartTime().plusMinutes(GRACE_MINUTES)).toMillis();

        Integer rid = reservation.getId();
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                producer.sendTimeoutCheck(rid, Math.max(delay, 0));
            }
        });

        return Result.ok("预约成功");
    }

    public boolean isSeatAvaliable(Integer seatId, LocalDateTime startTime, LocalDateTime endTime) {
        return this.lambdaQuery()
                .eq(Reservation::getSeatId, seatId)
                .lt(Reservation::getStartTime, endTime)
                .gt(Reservation::getEndTime, startTime)
                .ne(Reservation::getStatus, ReservationStatus.RELEASED)
                .list().isEmpty();
    }

    @Override
    @Transactional
    public Result cancelReservation(Integer id) {
        Reservation reservation = getById(id);
        if (reservation == null) {
            return Result.fail("预约不存在");
        }

        Seat seat = seatService.getById(reservation.getSeatId());
        if (seat != null) {
            seat.setStatus(SeatStatus.FREE);
            seatService.updateById(seat);
        }

        boolean removed = removeById(id);
        return removed ? Result.ok("取消成功") : Result.fail("取消失败");
    }

    @Override
    public Result listMyReservations() {
        List<Reservation> list = lambdaQuery()
                .eq(Reservation::getUserId, UserHolder.getCurrentUserId())
                .orderByDesc(Reservation::getId)
                .list();
        return Result.ok(list);
    }
}
