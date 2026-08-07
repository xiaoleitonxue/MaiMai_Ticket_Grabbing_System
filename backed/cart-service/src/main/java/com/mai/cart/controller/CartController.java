package com.mai.cart.controller;


import com.mai.cart.domain.dto.CartFormDTO;
import com.mai.cart.domain.po.Cart;
import com.mai.cart.domain.vo.CartVO;
import com.mai.cart.service.ICartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * <p>
 * 购物车控制器，负责处理购物车相关的HTTP请求，包括添加商品、更新数量、删除商品及查询购物车列表等操作
 * </p>
 */
@Api(tags = "购物车相关接口")
@RestController
@RequestMapping("/carts")
@RequiredArgsConstructor
public class CartController {
    private final ICartService cartService;

    /**
     * <p>
     * 添加商品到购物车，若商品已存在则更新数量，否则新增购物车条目
     * </p>
     *
     * @param cartFormDTO 购物车表单数据，包含商品ID和规格信息
     */
    @ApiOperation("添加商品到购物车")
    @PostMapping
    public void addItem2Cart(@Valid @RequestBody CartFormDTO cartFormDTO){
        cartService.addItem2Cart(cartFormDTO);
    }

    /**
     * <p>
     * 更新购物车中商品的数量或规格信息
     * </p>
     *
     * @param cart 购物车实体，包含要更新的字段
     */
    @ApiOperation("更新购物车数据")
    @PutMapping
    public void updateCart(@RequestBody Cart cart){
        cartService.updateById(cart);
    }

    /**
     * <p>
     * 根据购物车条目ID删除指定商品
     * </p>
     *
     * @param id 购物车条目的唯一标识
     */
    @ApiOperation("删除购物车中商品")
    @DeleteMapping("{id}")
    public void deleteCartItem(@Param ("购物车条目id")@PathVariable("id") Long id){
        cartService.removeById(id);
    }

    /**
     * <p>
     * 查询当前登录用户的购物车列表，包含商品详情和最新价格
     * </p>
     *
     * @return 当前用户的购物车商品列表
     */
    @ApiOperation("查询购物车列表")
    @GetMapping
    public List<CartVO> queryMyCarts(){
        return cartService.queryMyCarts();
    }

    /**
     * <p>
     * 批量删除购物车中指定ID集合的商品
     * </p>
     *
     * @param ids 要删除的购物车条目ID集合
     */
    @ApiOperation("批量删除购物车中商品")
    @ApiImplicitParam(name = "ids", value = "购物车条目id集合")
    @DeleteMapping
    public void deleteCartItemByIds(@RequestParam("ids") List<Long> ids){
        cartService.removeByItemIds(ids);
    }
}