package com.mai.trade.listener;

import com.mai.api.client.PayClient;
import com.mai.api.dto.PayOrderDTO;
import com.mai.trade.constants.TradeMqConstants;
import com.mai.trade.domain.po.Order;
import com.mai.trade.service.IOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * <p>
 * 订单延迟消息监听器，在订单创建延迟后检查支付状态，若已支付则标记成功，否则取消订单
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderDelayMessageListener {

    private final IOrderService orderService;
    private final PayClient payClient;

    /**
     * <p>
     * 监听订单延迟消息，通过查询支付微服务确认支付状态后决定标记支付成功或取消订单
     * </p>
     *
     * @param orderId 订单ID
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = TradeMqConstants.DELAY_ORDER_QUEUE, durable = "true"),
            exchange = @Exchange(value = TradeMqConstants.DELAY_EXCHANGE, type = "x-delayed-message",
                    arguments = @org.springframework.amqp.rabbit.annotation.Argument(name = "x-delayed-type", value = "topic")),
            key = TradeMqConstants.DELAY_ORDER_ROUTING_KEY
    ))
    public void listenOrderDelayMessage(Long orderId) {
        log.info("收到订单延迟消息，orderId: {}", orderId);

        // 1. 查询订单
        Order order = orderService.getById(orderId);
        if (order == null) {
            log.warn("订单不存在，orderId: {}", orderId);
            return;
        }

        // 2. 如果订单已支付(状态>=2)，则不用再更新
        if (order.getStatus() >= 2) {
            log.info("订单已支付，无需处理，orderId: {}, status: {}", orderId, order.getStatus());
            return;
        }

        // 3. 查询支付微服务的支付流水状态
        PayOrderDTO payOrder = payClient.queryPayOrderByBizOrderNo(orderId);

        // 4. 判断支付状态
        if (payOrder != null && payOrder.getStatus() == 3) {
            // 4.1 如果已支付，则更新交易订单的状态为已支付
            log.info("订单已支付成功，更新订单状态，orderId: {}", orderId);
            orderService.markOrderPaySuccess(orderId);
        } else {
            // 4.2 如果未支付，则需要取消订单
            log.info("订单未支付，取消订单，orderId: {}", orderId);
            orderService.cancelOrder(orderId);
        }
    }
}