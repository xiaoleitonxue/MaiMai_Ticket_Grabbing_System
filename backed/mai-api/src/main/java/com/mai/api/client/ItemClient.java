package com.mai.api.client;

import com.mai.api.config.DefaultFeignConfig;
import com.mai.api.dto.ItemDTO;
import com.mai.api.dto.OrderDetailDTO;
import com.mai.api.fallback.ItemClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@FeignClient(value = "item-service",fallbackFactory = ItemClientFallback.class, configuration = DefaultFeignConfig.class)
public interface ItemClient {

    @GetMapping("/items")
    List<ItemDTO> queryItemsByIds(@RequestParam("ids") Collection<Long> ids);

    @PutMapping("/items/stock/deduct")
    public void deductStock(@RequestBody List<OrderDetailDTO> items);

    @GetMapping("/items/{id}")
    ItemDTO queryItemById(@PathVariable("id") Long itemId);

}
