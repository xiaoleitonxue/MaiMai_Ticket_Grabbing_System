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

/**
 * <p>
 * 订单状态消息监听器，当订单创建成功后，异步清理用户购物车中已下单的商品
 * </p>
 */
@Component
public class OrderStatusListener {

    @Autowired
    private ICartService cartService;

    /**
     * <p>
     * 监听订单创建消息，根据消息中的商品ID列表和用户信息清理购物车
     * </p>
     *
     * @param itemIds 已下单的商品ID列表
     * @param userId 下单用户ID，从消息头中获取
     */
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