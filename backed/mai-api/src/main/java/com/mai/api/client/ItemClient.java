package com.mai.api.client;

import com.mai.api.config.DefaultFeignConfig;
import com.mai.api.dto.ItemDTO;
import com.mai.api.dto.OrderDetailDTO;
import com.mai.api.fallback.ItemClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 商品服务Feign客户端，提供商品批量查询、库存扣减及单品查询的远程调用接口
 * </p>
 */
@FeignClient(value = "item-service",fallbackFactory = ItemClientFallback.class, configuration = DefaultFeignConfig.class)
public interface ItemClient {

    /**
     * <p>
     * 根据商品ID集合批量查询商品信息
     * </p>
     *
     * @param ids 商品ID集合
     * @return 商品信息列表
     */
    @GetMapping("/items")
    List<ItemDTO> queryItemsByIds(@RequestParam("ids") Collection<Long> ids);

    /**
     * <p>
     * 批量扣减商品库存，用于下单时锁定库存
     * </p>
     *
     * @param items 订单明细列表，包含商品ID和扣减数量
     */
    @PutMapping("/items/stock/deduct")
    public void deductStock(@RequestBody List<OrderDetailDTO> items);

    /**
     * <p>
     * 根据商品ID查询单个商品详情
     * </p>
     *
     * @param itemId 商品ID
     * @return 商品详情
     */
    @GetMapping("/items/{id}")
    ItemDTO queryItemById(@PathVariable("id") Long itemId);

}