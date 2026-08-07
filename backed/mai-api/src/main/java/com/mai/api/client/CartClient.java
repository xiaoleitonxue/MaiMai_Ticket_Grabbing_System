package com.mai.api.client;

import com.mai.api.config.DefaultFeignConfig;
import com.mai.api.fallback.CartClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 购物车服务Feign客户端，提供购物车商品批量删除的远程调用接口
 * </p>
 */
@FeignClient(value = "cart-service", fallbackFactory = CartClientFallback.class, configuration = DefaultFeignConfig.class)
public interface CartClient {

    /**
     * <p>
     * 批量删除购物车中指定商品ID集合的条目
     * </p>
     *
     * @param ids 商品ID集合
     */
    @DeleteMapping("/carts")
    public void removeByItemIds(@RequestParam("ids") Collection<Long> ids);

}