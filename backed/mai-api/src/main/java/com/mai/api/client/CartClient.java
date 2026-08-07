package com.mai.api.client;

import com.mai.api.config.DefaultFeignConfig;
import com.mai.api.fallback.CartClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.List;

@FeignClient(value = "cart-service", fallbackFactory = CartClientFallback.class, configuration = DefaultFeignConfig.class)
public interface CartClient {

    @DeleteMapping("/carts")
    public void removeByItemIds(@RequestParam("ids") Collection<Long> ids);

}
