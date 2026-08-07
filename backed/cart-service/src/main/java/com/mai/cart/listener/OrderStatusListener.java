package com.mai.cart.listener;

import com.mai.cart.service.ICartService;
import com.mai.common.constants.MqConstants;
import com.mai.common.utils.UserContext;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderStatusListener {

    @Autowired
    private ICartService cartService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "cart.clear.queue", durable = "true"),
            exchange = @Exchange(value = MqConstants.TRADE_EXCHANGE_NAME, type = ExchangeTypes.TOPIC, durable = "true"),
            key = MqConstants.ROUTING_KEY_ORDER_CREATE
    ))
    public void listenOrderCreateMsg(List<Long> itemIds, @Header("user-info") Long userId) {

        UserContext.setUser(userId);

        cartService.removeByItemIds(itemIds);

        UserContext.removeUser();
    }

}
