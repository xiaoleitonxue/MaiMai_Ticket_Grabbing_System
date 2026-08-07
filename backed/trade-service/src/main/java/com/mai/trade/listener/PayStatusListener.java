package com.mai.trade.listener;

import com.mai.trade.service.IOrderService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>
 * 支付状态消息监听器，当支付成功后异步更新订单状态为已支付
 * </p>
 */
@Component
public class PayStatusListener {

    @Autowired
    private IOrderService orderService;

    /**
     * <p>
     * 监听支付成功消息，标记对应订单为已支付状态
     * </p>
     *
     * @param orderId 订单ID
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "mark.order.pay.queue", durable = "true"),
            exchange = @Exchange(value = "pay.topic", type = ExchangeTypes.TOPIC),
            key = "pay.success"
    ))
    public void linstenPaySuccessMsg(Long orderId) {
        orderService.markOrderPaySuccess(orderId);

    }
}