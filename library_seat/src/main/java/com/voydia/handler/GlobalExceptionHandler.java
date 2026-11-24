package com.voydia.handler;

import com.voydia.dto.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器类
 * 用于统一处理应用程序中抛出的各种异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public Result handle(Exception e) {
        log.error("系统异常", e);
        return Result.fail("系统异常，请稍后再试");
    }
}