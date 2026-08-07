package com.mai.item.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mai.common.domain.PageDTO;
import com.mai.common.domain.PageQuery;
import com.mai.common.utils.BeanUtils;
import com.mai.item.domain.dto.ItemDTO;
import com.mai.item.domain.dto.OrderDetailDTO;
import com.mai.item.domain.po.Item;
import com.mai.item.service.IItemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 商品管理控制器，提供商品的分页查询、批量查询、新增、更新、删除及库存扣减等接口
 * </p>
 */
@Api(tags = "商品管理相关接口")
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final IItemService itemService;

    /**
     * <p>
     * 分页查询商品列表，按更新时间降序排列
     * </p>
     *
     * @param query 分页查询条件
     * @return 商品分页数据
     */
    @ApiOperation("分页查询商品")
    @GetMapping("/page")
    public PageDTO<ItemDTO> queryItemByPage(PageQuery query) {
        // 1.分页查询
        Page<Item> result = itemService.page(query.toMpPage("update_time", false));
        // 2.封装并返回
        return PageDTO.of(result, ItemDTO.class);
    }

    /**
     * <p>
     * 根据商品ID集合批量查询商品信息
     * </p>
     *
     * @param ids 商品ID集合
     * @return 商品信息列表
     */
    @ApiOperation("根据id批量查询商品")
    @GetMapping
    public List<ItemDTO> queryItemByIds(@RequestParam("ids") List<Long> ids){
        return itemService.queryItemByIds(ids);
    }

    /**
     * <p>
     * 根据商品ID查询单个商品详情
     * </p>
     *
     * @param id 商品ID
     * @return 商品详情
     */
    @ApiOperation("根据id查询商品")
    @GetMapping("{id}")
    public ItemDTO queryItemById(@PathVariable("id") Long id) {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return BeanUtils.copyBean(itemService.getById(id), ItemDTO.class);
    }

    /**
     * <p>
     * 新增商品信息
     * </p>
     *
     * @param item 商品信息DTO
     */
    @ApiOperation("新增商品")
    @PostMapping
    public void saveItem(@RequestBody ItemDTO item) {
        // 新增
        itemService.save(BeanUtils.copyBean(item, Item.class));
    }

    /**
     * <p>
     * 更新商品上下架状态，并同步发送消息通知搜索服务
     * </p>
     *
     * @param id 商品ID
     * @param status 商品状态，1表示上架，其他表示下架
     */
    @ApiOperation("更新商品状态")
    @PutMapping("/status/{id}/{status}")
    public void updateItemStatus(@PathVariable("id") Long id, @PathVariable("status") Integer status){

        itemService.updateItemStatus(id, status);
    }

    /**
     * <p>
     * 更新商品信息，商品状态字段会被强制置空以忽略更新
     * </p>
     *
     * @param item 商品信息DTO
     */
    @ApiOperation("更新商品")
    @PutMapping
    public void updateItem(@RequestBody ItemDTO item) {
        // 不允许修改商品状态，所以强制设置为null，更新时，就会忽略该字段
        item.setStatus(null);
        // 更新
        itemService.updateById(BeanUtils.copyBean(item, Item.class));
    }

    /**
     * <p>
     * 根据商品ID删除商品
     * </p>
     *
     * @param id 商品ID
     */
    @ApiOperation("根据id删除商品")
    @DeleteMapping("{id}")
    public void deleteItemById(@PathVariable("id") Long id) {
        itemService.removeById(id);
    }

    /**
     * <p>
     * 批量扣减商品库存，用于下单场景
     * </p>
     *
     * @param items 订单明细列表，包含商品ID和扣减数量
     */
    @ApiOperation("批量扣减库存")
    @PutMapping("/stock/deduct")
    public void deductStock(@RequestBody List<OrderDetailDTO> items){
        itemService.deductStock(items);
    }


   }