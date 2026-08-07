package com.mai.trade.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mai.trade.domain.po.OrderLogistics;
import com.mai.trade.mapper.OrderLogisticsMapper;
import com.mai.trade.service.IOrderLogisticsService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 订单物流服务实现类，负责订单物流配送信息的数据持久化操作
 * </p>
 */
@Service
public class OrderLogisticsServiceImpl extends ServiceImpl<OrderLogisticsMapper, OrderLogistics> implements IOrderLogisticsService {

}