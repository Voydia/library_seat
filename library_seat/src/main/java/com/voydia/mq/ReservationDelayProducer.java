package com.voydia.mq;

import com.voydia.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ReservationDelayProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * @param reservationId 预约 id
     * @param delayMillis   延迟毫秒数
     */
    public void sendTimeoutCheck(Integer reservationId, long delayMillis) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.ROUTING_KEY,
                reservationId,
                message -> {
                    message.getMessageProperties().setHeader("x-delay", Math.max(delayMillis, 0L));
                    return message;
                });
    }
}