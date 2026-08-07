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

@FeignClient(value = "trade-service", fallbackFactory = TradeClientFallback.class, configuration = DefaultFeignConfig.class)
public interface TradeClient {

    @PutMapping("/orders/{orderId}")
    public void markOrderPaySuccess(@PathVariable("orderId") Long orderId);

}
