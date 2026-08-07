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

@Api(tags = "搜索相关接口")
@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {
    private final ISearchService searchService;
    @ApiOperation("搜索商品")
    @GetMapping("/list")
    public PageDTO<ItemDTO> search(ItemPageQuery query) {
        return searchService.search(query);
    }

    @ApiOperation("动态过滤器")
    @PostMapping("/filters")
    public CategoryAndBrandDTO filters(@RequestBody ItemPageQuery query){
        return searchService.filters(query);
    }
}