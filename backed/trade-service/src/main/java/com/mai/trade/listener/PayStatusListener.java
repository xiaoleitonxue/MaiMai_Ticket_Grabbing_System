package com.mai.trade.listener;

import com.mai.trade.service.IOrderService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PayStatusListener {

    @Autowired
    private IOrderService orderService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "mark.order.pay.queue", durable = "true"),
            exchange = @Exchange(value = "pay.topic", type = ExchangeTypes.TOPIC),
            key = "pay.success"
    ))
    public void linstenPaySuccessMsg(Long orderId) {
        orderService.markOrderPaySuccess(orderId);

    }
}
