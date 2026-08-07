package com.mai.cart.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mai.cart.domain.po.Cart;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * <p>
 * 购物车数据访问层，提供购物车商品的自定义数据库操作
 * </p>
 */
public interface CartMapper extends BaseMapper<Cart> {

    /**
     * <p>
     * 将指定用户和商品对应的购物车条目数量加一
     * </p>
     *
     * @param itemId 商品ID
     * @param userId 用户ID
     */
    @Update("UPDATE cart SET num = num + 1 WHERE user_id = #{userId} AND item_id = #{itemId}")
    void updateNum(@Param("itemId") Long itemId, @Param("userId") Long userId);
}