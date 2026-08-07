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
 * 订单管理接口
 * </p>
 *
 * @author 虎哥
 * @since 2023-05-05
 */
@Api(tags = "订单管理接口")
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final IOrderService orderService;

    @ApiOperation("根据id查询订单")
    @GetMapping("{id}")
    public OrderVO queryOrderById(@Param ("订单id")@PathVariable("id") Long orderId) {
        return BeanUtils.copyBean(orderService.getById(orderId), OrderVO.class);
    }

    @ApiOperation("查询当前用户的所有订单")
    @GetMapping("/list")
    public List<OrderVO> queryMyOrders() {
        Long userId = UserContext.getUser();
        return orderService.queryOrdersByUserId(userId);
    }

    @ApiOperation("管理端分页查询所有订单")
    @GetMapping("/page")
    public PageDTO<OrderVO> queryOrdersByPage(
            @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        return orderService.queryOrdersByPage(pageNo, pageSize);
    }

    @ApiOperation("创建订单")
    @PostMapping
    public Long createOrder(@RequestBody OrderFormDTO orderFormDTO){
        return orderService.createOrder(orderFormDTO);
    }

    @ApiOperation("标记订单已支付")
    @ApiImplicitParam(name = "orderId", value = "订单id", paramType = "path")
    @PutMapping("/{orderId}")
    public void markOrderPaySuccess(@PathVariable("orderId") Long orderId) {
        orderService.markOrderPaySuccess(orderId);
    }
}