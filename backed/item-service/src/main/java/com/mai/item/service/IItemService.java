package com.mai.item.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mai.item.domain.dto.ItemDTO;
import com.mai.item.domain.dto.OrderDetailDTO;
import com.mai.item.domain.po.Item;

import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 商品表 服务类
 * </p>
 *
 * @author 虎哥
 * @since 2023-05-05
 */
public interface IItemService extends IService<Item> {

    void deductStock(List<OrderDetailDTO> items);

    List<ItemDTO> queryItemByIds(Collection<Long> ids);

    void updateItemStatus(Long id, Integer status);
}