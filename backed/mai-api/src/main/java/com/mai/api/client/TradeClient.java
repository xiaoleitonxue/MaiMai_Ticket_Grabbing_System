package com.mai.api.client;

import com.mai.api.config.DefaultFeignConfig;
import com.mai.api.fallback.TradeClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 交易服务Feign客户端，提供订单支付状态标记的远程调用接口
 * </p>
 */
@FeignClient(value = "trade-service", fallbackFactory = TradeClientFallback.class, configuration = DefaultFeignConfig.class)
public interface TradeClient {

    /**
     * <p>
     * 标记订单为已支付状态
     * </p>
     *
     * @param orderId 订单ID
     */
    @PutMapping("/orders/{orderId}")
    public void markOrderPaySuccess(@PathVariable("orderId") Long orderId);

}