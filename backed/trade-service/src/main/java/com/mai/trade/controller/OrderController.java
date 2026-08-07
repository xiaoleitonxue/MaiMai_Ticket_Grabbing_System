package com.mai.trade.controller;

import com.mai.common.utils.BeanUtils;
import com.mai.common.utils.UserContext;
import com.mai.common.domain.PageDTO;
import com.mai.trade.domain.dto.OrderFormDTO;
import com.mai.trade.domain.vo.OrderVO;
import com.mai.trade.service.IOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 订单管理控制器，提供订单的创建、查询、支付状态标记等HTTP接口
 * </p>
 */
@Api(tags = "订单管理接口")
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final IOrderService orderService;

    /**
     * <p>
     * 根据订单ID查询订单详情
     * </p>
     *
     * @param orderId 订单ID
     * @return 订单视图对象
     */
    @ApiOperation("根据id查询订单")
    @GetMapping("{id}")
    public OrderVO queryOrderById(@Param ("订单id")@PathVariable("id") Long orderId) {
        return BeanUtils.copyBean(orderService.getById(orderId), OrderVO.class);
    }

    /**
     * <p>
     * 查询当前登录用户的所有订单，按创建时间倒序排列
     * </p>
     *
     * @return 当前用户的订单列表
     */
    @ApiOperation("查询当前用户的所有订单")
    @GetMapping("/list")
    public List<OrderVO> queryMyOrders() {
        Long userId = UserContext.getUser();
        return orderService.queryOrdersByUserId(userId);
    }

    /**
     * <p>
     * 管理端分页查询所有订单
     * </p>
     *
     * @param pageNo 当前页码，默认为1
     * @param pageSize 每页大小，默认为10
     * @return 订单分页数据
     */
    @ApiOperation("管理端分页查询所有订单")
    @GetMapping("/page")
    public PageDTO<OrderVO> queryOrdersByPage(
            @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        return orderService.queryOrdersByPage(pageNo, pageSize);
    }

    /**
     * <p>
     * 创建订单，包含商品校验、价格计算、订单保存、购物车清理及库存扣减等完整流程
     * </p>
     *
     * @param orderFormDTO 订单表单数据，包含订单明细和支付方式
     * @return 新创建的订单ID
     */
    @ApiOperation("创建订单")
    @PostMapping
    public Long createOrder(@RequestBody OrderFormDTO orderFormDTO){
        return orderService.createOrder(orderFormDTO);
    }

    /**
     * <p>
     * 标记订单为已支付状态，更新支付时间
     * </p>
     *
     * @param orderId 订单ID
     */
    @ApiOperation("标记订单已支付")
    @ApiImplicitParam(name = "orderId", value = "订单id", paramType = "path")
    @PutMapping("/{orderId}")
    public void markOrderPaySuccess(@PathVariable("orderId") Long orderId) {
        orderService.markOrderPaySuccess(orderId);
    }
}