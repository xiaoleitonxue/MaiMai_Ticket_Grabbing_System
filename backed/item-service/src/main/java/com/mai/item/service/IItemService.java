package com.mai.item.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mai.item.domain.dto.ItemDTO;
import com.mai.item.domain.dto.OrderDetailDTO;
import com.mai.item.domain.po.Item;

import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 商品服务接口，提供商品库存扣减、批量查询及状态更新等核心业务操作
 * </p>
 */
public interface IItemService extends IService<Item> {

    /**
     * <p>
     * 批量扣减商品库存，用于下单时锁定库存
     * </p>
     *
     * @param items 订单明细列表，包含商品ID和扣减数量
     * @throws BizIllegalException 当库存不足时抛出
     */
    void deductStock(List<OrderDetailDTO> items);

    /**
     * <p>
     * 根据商品ID集合批量查询商品信息
     * </p>
     *
     * @param ids 商品ID集合
     * @return 商品信息DTO列表
     */
    List<ItemDTO> queryItemByIds(Collection<Long> ids);

    /**
     * <p>
     * 更新商品上下架状态，并发送消息通知搜索服务同步索引
     * </p>
     *
     * @param id 商品ID
     * @param status 商品状态，1表示上架，其他表示下架
     */
    void updateItemStatus(Long id, Integer status);
}