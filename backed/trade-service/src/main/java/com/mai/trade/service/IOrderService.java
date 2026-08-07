package com.mai.trade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mai.common.domain.PageDTO;
import com.mai.trade.domain.dto.OrderFormDTO;
import com.mai.trade.domain.po.Order;
import com.mai.trade.domain.vo.OrderVO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 虎哥
 * @since 2023-05-05
 */
public interface IOrderService extends IService<Order> {

    Long createOrder(OrderFormDTO orderFormDTO);

    void markOrderPaySuccess(Long orderId);

    /**
     * 取消订单
     * @param orderId 订单id
     */
    void cancelOrder(Long orderId);

    /**
     * 查询用户的所有订单
     * @param userId 用户id
     * @return 订单VO列表
     */
    List<OrderVO> queryOrdersByUserId(Long userId);

    /**
     * 分页查询所有订单（管理端）
     * @param pageNo 页码
     * @param pageSize 每页大小
     * @return 订单VO分页数据
     */
    PageDTO<OrderVO> queryOrdersByPage(Integer pageNo, Integer pageSize);
}