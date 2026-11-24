package com.voydia.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "reservation.exchange";
    public static final String QUEUE = "reservation.delay.queue";
    public static final String ROUTING_KEY = "reservation.delay";

    @Bean
    public DirectExchange reservationExchange() {
        return new DirectExchange(EXCHANGE, true, false);
    }

    /**
     * 必须是 quorum 队列，原生延迟消息只支持 quorum
     */
    @Bean
    public Queue reservationQueue() {
        return QueueBuilder.durable(QUEUE).quorum().build();
    }

    @Bean
    public Binding reservationBinding() {
        return BindingBuilder.bind(reservationQueue())
                .to(reservationExchange())
                .with(ROUTING_KEY);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }
}