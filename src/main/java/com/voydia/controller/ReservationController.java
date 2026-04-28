package com.voydia.controller;

import com.voydia.entity.Reservation;
import com.voydia.service.ReservationService;
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
    private ReservationService reservationService;

    @Operation(summary = "全部预约记录")
    @GetMapping("/all")
    public List<Reservation> getAllReservation(){
        return reservationService.list();
    }

    @Operation(summary = "根据id查预约")
    @GetMapping("/{id}")
    public Reservation getById(@PathVariable Integer id){
        return reservationService.getById(id);
    }

    @Operation(summary = "新增预约")
    @PostMapping
    public boolean createReservation(@RequestBody Reservation reservation){
        return reservationService.save(reservation);
    }

    @Operation(summary = "删除预约")
    @DeleteMapping
    public boolean cancelReservation(@RequestBody Reservation reservation){
        return reservationService.cancelReservation(reservation.getId());
    }


}
