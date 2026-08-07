package com.mai.trade.config;

import com.mai.trade.constants.TradeMqConstants;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.CustomExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQConfig {

    /**
     * 创建延迟交换机（x-delayed-message类型）
     */
    @Bean
    public CustomExchange delayExchange() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-delayed-type", "topic");
        return new CustomExchange(TradeMqConstants.DELAY_EXCHANGE, "x-delayed-message", true, false, args);
    }

    /**
     * 创建延迟队列
     */
    @Bean
    public Queue delayOrderQueue() {
        return new Queue(TradeMqConstants.DELAY_ORDER_QUEUE, true);
    }

    /**
     * 绑定延迟队列到延迟交换机
     */
    @Bean
    public Binding delayOrderBinding() {
        return BindingBuilder.bind(delayOrderQueue())
                .to(delayExchange())
                .with(TradeMqConstants.DELAY_ORDER_ROUTING_KEY)
                .noargs();
    }
}