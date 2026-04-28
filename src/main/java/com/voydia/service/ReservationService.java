package com.voydia.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.voydia.entity.Reservation;
import com.voydia.entity.Seat;
import com.voydia.mapper.ReservationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;

@Service
public class ReservationService extends ServiceImpl<ReservationMapper, Reservation> {

    @Autowired
    private SeatService seatService;
    //检查冲突
    public boolean createReservation(Reservation reservation) {
         if(isSeatAvaliable(reservation.getSeatId(),reservation.getStartTime(),reservation.getEndTime())){
             boolean saved =save(reservation);
             if(saved){
                 Seat seat = seatService.getById(reservation.getSeatId());
                 seat.setStatus(1);
                 seatService.updateById(seat);
             }
             return saved;
         } else {
             return false;
         }
     }
     public boolean isSeatAvaliable(Integer seatId, LocalDateTime startTime,LocalDateTime endTime) {
         return this.lambdaQuery()
                 .eq(Reservation::getSeatId, seatId)
                 .lt(Reservation::getStartTime, endTime)
                 .gt(Reservation::getEndTime, startTime)
                 .list().isEmpty();
     }

     public boolean cancelReservation(Integer id) {
        Reservation reservation = getById(id);
        if(reservation==null) return false;
        Seat seat = seatService.getById(reservation.getSeatId());
        if(seat!=null){
            seat.setStatus(0);
            seatService.updateById(seat);//保存到数据库
        }
        boolean removed = removeById(id);
        return removed;
     }

}
