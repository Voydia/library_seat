package com.voydia.controller;

import com.voydia.dto.Result;
import com.voydia.entity.Seat;
import com.voydia.service.ISeatService;
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
    private ISeatService seatService;


    @Operation(summary = "获取所有座位")
    @GetMapping("/all")
    public Result getAllSeats() {
        return Result.ok(seatService.list());
    }

    @Operation (summary = "根据id获取座位")
    @GetMapping("/{id}")
    public Result getSeatById(@PathVariable Integer id) {
        return Result.ok(seatService.getById(id));
    }

    @Operation(summary = "添加座位")
    @PostMapping
    public Result addSeat(@RequestBody Seat seat) {
        return Result.ok(seatService.save(seat));
    }

    @Operation(summary = "删除座位")
    @DeleteMapping("/{id}")
    public Result deleteSeat(@PathVariable Integer id) {
        return Result.ok(seatService.removeById(id));
    }
    @Operation(summary = "更新座位")
    @PutMapping
    public Result updateSeat(@RequestBody Seat seat) {
        return Result.ok(seatService.updateById(seat));
    }
}
