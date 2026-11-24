package com.voydia.controller;

import com.voydia.dto.Result;
import com.voydia.entity.Reservation;
import com.voydia.service.IReservationService;
import com.voydia.service.ReservationApplicationService;
import com.voydia.util.UserHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservation" )
@Tag(name = "预约管理" , description = "预约相关接口")
public class ReservationController {

    @Autowired
    private IReservationService reservationService;
    @Autowired
    private ReservationApplicationService reservationApplicationService;

    @Operation(summary = "新增预约")
    @PostMapping
    public Result createReservation(@RequestBody Reservation reservation){
        reservation.setUserId(UserHolder.getCurrentUserId());
        return reservationApplicationService.createReservation(reservation);
    }

    @Operation(summary = "删除预约")
    @DeleteMapping("/{id}")
    public Result cancelReservation(@PathVariable Integer id){
        Reservation r = reservationService.getById(id);
        if(r == null){
            return Result.fail("预约不存在");
        }
        if(!UserHolder.getCurrentUserId().equals(r.getUserId())){
            return Result.fail("无权取消他人的预约");
        }
        return reservationService.cancelReservation(id);
    }

    @Operation(summary = "签到")
    @PutMapping("/checkin/{id}")
    public Result checkIn(@PathVariable Integer id){
        return reservationService.checkin(id);
    }

    @Operation(summary = "我的预约")
    @GetMapping("/my")
    public Result myReservations() {
        return reservationService.listMyReservations();
    }




}
