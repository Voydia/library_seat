package com.voydia.mq;

import com.voydia.config.RabbitMQConfig;
import com.voydia.service.impl.ReservationServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ReservationTimeoutListener {

    @Autowired
    private ReservationServiceImpl reservationService;

    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void onTimeout(Integer reservationId) {
        log.info("收到延迟消息，检查预约 {}", reservationId);
        reservationService.releaseIfNotCheckedIn(reservationId);
    }
}