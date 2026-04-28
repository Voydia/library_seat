package com.voydia.controller;

import com.voydia.entity.Seat;
import com.voydia.service.SeatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/seat")
@Tag(name = "座位管理", description = "座位管理")
public class SeatController {
    @Autowired
    private SeatService seatService;


    @Operation(summary = "获取所有座位")
    @GetMapping("/all")
    public List<Seat> getAllSeats() {
        return seatService.list();
    }

    @Operation (summary = "根据id获取座位")
    @GetMapping("/{id}")
    public Seat getSeatById(@PathVariable Integer id) {
        return seatService.getById(id);
    }

    @Operation(summary = "添加座位")
    @PostMapping
    public boolean addSeat(@RequestBody Seat seat) {
        return seatService.save(seat);
    }

    @Operation(summary = "删除座位")
    @DeleteMapping("/{id}")
    public boolean deleteSeat(@PathVariable Integer id) {
        return seatService.removeById(id);
    }
    @Operation(summary = "更新座位")
    @PutMapping
    public boolean updateSeat(@RequestBody Seat seat) {
        return seatService.updateById(seat);
    }
}
