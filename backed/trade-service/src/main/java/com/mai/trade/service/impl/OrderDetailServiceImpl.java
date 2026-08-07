package com.mai.trade.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mai.trade.domain.po.OrderDetail;
import com.mai.trade.mapper.OrderDetailMapper;
import com.mai.trade.service.IOrderDetailService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 订单详情服务实现类，负责订单商品条目的数据持久化操作
 * </p>
 */
@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements IOrderDetailService {

}