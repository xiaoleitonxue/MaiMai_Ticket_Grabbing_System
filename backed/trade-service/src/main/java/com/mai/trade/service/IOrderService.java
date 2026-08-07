package com.mai.trade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mai.common.domain.PageDTO;
import com.mai.trade.domain.dto.OrderFormDTO;
import com.mai.trade.domain.po.Order;
import com.mai.trade.domain.vo.OrderVO;

import java.util.List;

/**
 * <p>
 * 订单服务接口，提供订单的创建、支付标记、取消及查询等核心业务操作
 * </p>
 */
public interface IOrderService extends IService<Order> {

    /**
     * <p>
     * 创建订单，包含商品校验、价格计算、订单保存、购物车清理及库存扣减等完整流程
     * </p>
     *
     * @param orderFormDTO 订单表单数据，包含订单明细和支付方式
     * @return 新创建的订单ID
     */
    Long createOrder(OrderFormDTO orderFormDTO);

    /**
     * <p>
     * 标记订单为已支付状态，更新支付时间
     * </p>
     *
     * @param orderId 订单ID
     */
    void markOrderPaySuccess(Long orderId);

    /**
     * <p>
     * 取消订单，恢复已扣减的商品库存并将订单状态更新为已关闭
     * </p>
     *
     * @param orderId 订单ID
     */
    void cancelOrder(Long orderId);

    /**
     * <p>
     * 查询指定用户的所有订单及订单详情
     * </p>
     *
     * @param userId 用户ID
     * @return 订单视图列表，包含订单详情和物流信息
     */
    List<OrderVO> queryOrdersByUserId(Long userId);

    /**
     * <p>
     * 管理端分页查询所有订单及订单详情
     * </p>
     *
     * @param pageNo 当前页码
     * @param pageSize 每页大小
     * @return 订单分页数据
     */
    PageDTO<OrderVO> queryOrdersByPage(Integer pageNo, Integer pageSize);
}