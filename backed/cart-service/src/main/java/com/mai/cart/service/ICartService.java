package com.mai.cart.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mai.cart.domain.dto.CartFormDTO;
import com.mai.cart.domain.po.Cart;
import com.mai.cart.domain.vo.CartVO;

import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 购物车服务接口，提供购物车商品的添加、查询及批量删除等核心业务操作
 * </p>
 */
public interface ICartService extends IService<Cart> {

    /**
     * <p>
     * 添加商品到购物车，若商品已存在则更新数量，否则新增条目
     * </p>
     *
     * @param cartFormDTO 购物车表单数据，包含商品ID和规格信息
     */
    void addItem2Cart(CartFormDTO cartFormDTO);

    /**
     * <p>
     * 查询当前登录用户的购物车列表，包含商品最新价格和库存状态
     * </p>
     *
     * @return 当前用户的购物车商品视图列表
     */
    List<CartVO> queryMyCarts();

    /**
     * <p>
     * 根据商品ID集合批量删除当前用户购物车中的对应条目
     * </p>
     *
     * @param itemIds 待删除的商品ID集合
     */
    void removeByItemIds(Collection<Long> itemIds);
}