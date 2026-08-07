package com.mai.trade.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mai.api.client.CartClient;
import com.mai.api.dto.OrderDetailDTO;
import com.mai.common.constants.MqConstants;
import com.mai.common.exception.BadRequestException;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mai.common.domain.PageDTO;
import com.mai.common.utils.BeanUtils;
import com.mai.common.utils.UserContext;
import com.mai.api.dto.ItemDTO;
import com.mai.trade.constants.TradeMqConstants;
import com.mai.trade.domain.dto.OrderFormDTO;
import com.mai.trade.domain.po.Order;
import com.mai.trade.domain.po.OrderDetail;
import com.mai.trade.domain.vo.OrderVO;
import com.mai.trade.domain.vo.OrderDetailVO;
import com.mai.trade.mapper.OrderMapper;
import com.mai.api.client.ItemClient;
import com.mai.trade.service.IOrderDetailService;
import com.mai.trade.service.IOrderService;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 订单服务实现类，负责订单创建、支付标记、取消及查询等核心业务逻辑，涉及远程调用商品服务、购物车服务和支付服务
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {

    private final ItemClient itemClient;
    private final IOrderDetailService detailService;
    private final CartClient cartClient;
    private final RabbitTemplate rabbitTemplate;

    /**
     * <p>
     * 创建订单，完整流程包含：商品校验、价格计算、订单保存、购物车清理及库存扣减，使用分布式事务保证一致性
     * </p>
     *
     * @param orderFormDTO 订单表单数据，包含订单明细和支付方式
     * @return 新创建的订单ID
     * @throws BadRequestException 当商品不存在时抛出
     */
    @Override
//    @Transactional
    @GlobalTransactional
    public Long createOrder(OrderFormDTO orderFormDTO) {
        // 1.订单数据
        Order order = new Order();
        // 1.1.查询商品
        List<OrderDetailDTO> detailDTOS = orderFormDTO.getDetails();
        // 1.2.获取商品id和数量的Map
        Map<Long, Integer> itemNumMap = detailDTOS.stream()
                .collect(Collectors.toMap(OrderDetailDTO::getItemId, OrderDetailDTO::getNum));
        Set<Long> itemIds = itemNumMap.keySet();
        // 1.3.查询商品
        List<ItemDTO> items = itemClient.queryItemsByIds(itemIds);
        if (items == null || items.size() < itemIds.size()) {
            throw new BadRequestException("商品不存在");
        }
        // 1.4.基于商品价格、购买数量计算商品总价：totalFee
        int total = 0;
        for (ItemDTO item : items) {
            total += item.getPrice() * itemNumMap.get(item.getId());
        }
        order.setTotalFee(total);
        // 1.5.其它属性
        order.setPaymentType(orderFormDTO.getPaymentType());
        order.setUserId(UserContext.getUser());
        order.setStatus(1);
        // 1.6.将Order写入数据库order表中
        save(order);

        // 2.保存订单详情
        List<OrderDetail> details = buildDetails(order.getId(), items, itemNumMap);
        detailService.saveBatch(details);

        // 3.清理购物车商品
        //cartClient.removeByItemIds(itemIds);
        rabbitTemplate.convertAndSend(MqConstants.TRADE_EXCHANGE_NAME, MqConstants.ROUTING_KEY_ORDER_CREATE, itemIds, new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                message.getMessageProperties().setHeader("user-info", UserContext.getUser());
                return message;
            }
        });


//        int i = 1/0;

        // 4.扣减库存
        try {
            itemClient.deductStock(detailDTOS);
        } catch (Exception e) {
            throw new RuntimeException("库存不足！");
        }

        rabbitTemplate.convertAndSend(TradeMqConstants.DELAY_EXCHANGE, TradeMqConstants.DELAY_ORDER_ROUTING_KEY, order.getId(), new MessagePostProcessor(){
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                message.getMessageProperties().setDelay(10000);
                return message;
            }
        });

        return order.getId();
    }

    /**
     * <p>
     * 标记订单为已支付状态，使用乐观锁确保仅在订单状态为未支付时更新
     * </p>
     *
     * @param orderId 订单ID
     */
    @Override
    public void markOrderPaySuccess(Long orderId) {
    /*// 查询订单
    Order oldOrder = getById(orderId);

    // 如果订单非未支付的话则不更新
    if (oldOrder == null || oldOrder.getStatus() > 1) {
        return;
    }

    Order order = new Order();
    order.setId(orderId);
    order.setStatus(2);
    order.setPayTime(LocalDateTime.now());
    updateById(order);*/

        // update order set status = 2, pay_time = now() where id = orderId and status = 1
        lambdaUpdate().set(Order::getStatus, 2)
                .set(Order::getPayTime, LocalDateTime.now())
                .eq(Order::getStatus, 1)
                .eq(Order::getId, orderId)
                .update();
    }

    /**
     * <p>
     * 取消订单，恢复已扣减的商品库存（将数量设为负值后调用商品服务归还），并将订单状态更新为已关闭，使用分布式事务
     * </p>
     *
     * @param orderId 订单ID
     */
    @Override
    @GlobalTransactional
    public void cancelOrder(Long orderId) {
        // 1. 查询订单
        Order order = getById(orderId);
        if (order == null || order.getStatus() != 1) {
            return;
        }

        //- 将查询该订单对于的商品列表
        List<OrderDetail> orderDetailList = detailService.lambdaQuery().eq(OrderDetail::getOrderId, orderId).list();
        List<OrderDetailDTO> orderDetailDTOList = BeanUtils.copyList(orderDetailList, OrderDetailDTO.class);
        //- 将上述商品对于的购买数量返还到 商品微服务item-service  ---> 优化：利用已有的一些接口实现 返还库存
        //将购买的商品数量变为负值
        for (OrderDetailDTO orderDetailDTO : orderDetailDTOList) {
            orderDetailDTO.setNum(-orderDetailDTO.getNum());
        }
        itemClient.deductStock(orderDetailDTOList);

        // 2. 更新订单状态为取消(5)
        lambdaUpdate()
                .set(Order::getStatus, 5)
                .set(Order::getCloseTime, LocalDateTime.now())
                .eq(Order::getId, orderId)
                .eq(Order::getStatus, 1)
                .update();

        log.info("订单已取消，orderId: {}", orderId);
    }

    /**
     * <p>
     * 查询指定用户的所有订单及订单详情，按创建时间倒序排列
     * </p>
     *
     * @param userId 用户ID
     * @return 订单视图列表，包含订单详情
     */
    @Override
    public List<OrderVO> queryOrdersByUserId(Long userId) {
        List<Order> orders = lambdaQuery().eq(Order::getUserId, userId).orderByDesc(Order::getCreateTime).list();
        if (orders == null || orders.isEmpty()) {
            return new ArrayList<>();
        }
        List<Long> orderIds = orders.stream().map(Order::getId).collect(Collectors.toList());
        List<OrderDetail> allDetails = detailService.lambdaQuery().in(OrderDetail::getOrderId, orderIds).list();
        Map<Long, List<OrderDetail>> detailMap = allDetails.stream()
                .collect(Collectors.groupingBy(OrderDetail::getOrderId));

        List<OrderVO> orderVOList = new ArrayList<>();
        for (Order order : orders) {
            OrderVO vo = BeanUtils.copyBean(order, OrderVO.class);
            List<OrderDetail> detailList = detailMap.get(order.getId());
            if (detailList != null) {
                List<OrderDetailVO> detailVOList = detailList.stream().map(d -> {
                    OrderDetailVO detailVO = new OrderDetailVO();
                    detailVO.setItemId(d.getItemId());
                    detailVO.setNum(d.getNum());
                    detailVO.setName(d.getName());
                    detailVO.setSpec(d.getSpec());
                    detailVO.setPrice(d.getPrice());
                    detailVO.setImage(d.getImage());
                    return detailVO;
                }).collect(Collectors.toList());
                vo.setDetails(detailVOList);
            }
            orderVOList.add(vo);
        }
        return orderVOList;
    }

    /**
     * <p>
     * 管理端分页查询所有订单及订单详情，按创建时间倒序排列
     * </p>
     *
     * @param pageNo 当前页码
     * @param pageSize 每页大小
     * @return 订单分页数据
     */
    @Override
    public PageDTO<OrderVO> queryOrdersByPage(Integer pageNo, Integer pageSize) {
        Page<Order> page = lambdaQuery().orderByDesc(Order::getCreateTime).page(new Page<>(pageNo, pageSize));
        List<Order> orders = page.getRecords();
        if (orders == null || orders.isEmpty()) {
            return PageDTO.empty(page);
        }
        List<Long> orderIds = orders.stream().map(Order::getId).collect(Collectors.toList());
        List<OrderDetail> allDetails = detailService.lambdaQuery().in(OrderDetail::getOrderId, orderIds).list();
        Map<Long, List<OrderDetail>> detailMap = allDetails.stream()
                .collect(Collectors.groupingBy(OrderDetail::getOrderId));

        List<OrderVO> voList = orders.stream().map(order -> {
            OrderVO vo = BeanUtils.copyBean(order, OrderVO.class);
            List<OrderDetail> detailList = detailMap.get(order.getId());
            if (detailList != null) {
                List<OrderDetailVO> detailVOList = detailList.stream().map(d -> {
                    OrderDetailVO detailVO = new OrderDetailVO();
                    detailVO.setItemId(d.getItemId());
                    detailVO.setNum(d.getNum());
                    detailVO.setName(d.getName());
                    detailVO.setSpec(d.getSpec());
                    detailVO.setPrice(d.getPrice());
                    detailVO.setImage(d.getImage());
                    return detailVO;
                }).collect(Collectors.toList());
                vo.setDetails(detailVOList);
            }
            return vo;
        }).collect(Collectors.toList());

        return PageDTO.of(page, voList);
    }

    private List<OrderDetail> buildDetails(Long orderId, List<ItemDTO> items, Map<Long, Integer> numMap) {
        List<OrderDetail> details = new ArrayList<>(items.size());
        for (ItemDTO item : items) {
            OrderDetail detail = new OrderDetail();
            detail.setName(item.getName());
            detail.setSpec(item.getSpec());
            detail.setPrice(item.getPrice());
            detail.setNum(numMap.get(item.getId()));
            detail.setItemId(item.getId());
            detail.setImage(item.getImage());
            detail.setOrderId(orderId);
            details.add(detail);
        }
        return details;
    }
}