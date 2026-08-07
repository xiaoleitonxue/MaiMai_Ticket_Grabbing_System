package com.mai.search.controller;


import com.mai.api.dto.ItemDTO;
import com.mai.common.domain.PageDTO;
import com.mai.search.domain.dto.CategoryAndBrandDTO;
import com.mai.search.domain.po.ItemDoc;
import com.mai.search.domain.query.ItemPageQuery;
import com.mai.search.service.ISearchService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.search.SearchService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 搜索控制器，提供商品搜索和动态过滤器聚合查询接口
 * </p>
 */
@Api(tags = "搜索相关接口")
@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {
    private final ISearchService searchService;

    /**
     * <p>
     * 全文搜索商品，支持关键词匹配、品牌分类过滤、价格范围筛选及广告加权排序
     * </p>
     *
     * @param query 搜索查询条件，包含关键词、品牌、分类、价格区间及分页参数
     * @return 搜索结果分页数据
     */
    @ApiOperation("搜索商品")
    @GetMapping("/list")
    public PageDTO<ItemDTO> search(ItemPageQuery query) {
        return searchService.search(query);
    }

    /**
     * <p>
     * 获取动态过滤器，对搜索结果进行品牌和分类的聚合统计
     * </p>
     *
     * @param query 搜索查询条件，用于确定聚合范围
     * @return 品牌和分类的聚合结果
     */
    @ApiOperation("动态过滤器")
    @PostMapping("/filters")
    public CategoryAndBrandDTO filters(@RequestBody ItemPageQuery query){
        return searchService.filters(query);
    }
}