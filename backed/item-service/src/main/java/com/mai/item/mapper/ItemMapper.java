package com.mai.item.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mai.item.domain.dto.OrderDetailDTO;
import com.mai.item.domain.po.Item;
import org.apache.ibatis.annotations.Update;

/**
 * <p>
 * 商品数据访问层，提供商品库存扣减等自定义数据库操作
 * </p>
 */
public interface ItemMapper extends BaseMapper<Item> {

    /**
     * <p>
     * 根据订单明细扣减对应商品的库存数量
     * </p>
     *
     * @param orderDetail 订单明细，包含商品ID和扣减数量
     */
    @Update("UPDATE item SET stock = stock - #{num} WHERE id = #{itemId}")
    void updateStock(OrderDetailDTO orderDetail);
}