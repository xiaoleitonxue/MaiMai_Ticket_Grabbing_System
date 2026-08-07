package com.mai.search.service;

import com.mai.api.dto.ItemDTO;
import com.mai.common.domain.PageDTO;
import com.mai.search.domain.dto.CategoryAndBrandDTO;
import com.mai.search.domain.po.ItemDoc;
import com.mai.search.domain.query.ItemPageQuery;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 搜索服务接口，提供商品全文搜索、动态过滤器聚合及索引同步等核心业务操作
 * </p>
 */
public interface ISearchService {

    /**
     * <p>
     * 全文搜索商品，支持关键词匹配、品牌分类过滤、价格范围筛选及广告加权排序
     * </p>
     *
     * @param query 搜索查询条件，包含关键词、品牌、分类、价格区间及分页参数
     * @return 搜索结果分页数据
     */
    PageDTO<ItemDTO> search(ItemPageQuery query);

    /**
     * <p>
     * 获取动态过滤器，对搜索结果进行品牌和分类的聚合统计
     * </p>
     *
     * @param query 搜索查询条件，用于确定聚合范围
     * @return 品牌和分类的聚合结果
     */
    CategoryAndBrandDTO filters(ItemPageQuery query);

    /**
     * <p>
     * 保存商品到Elasticsearch索引，用于商品上架时同步数据
     * </p>
     *
     * @param itemId 商品ID
     */
    void saveItemById(Long itemId);

    /**
     * <p>
     * 从Elasticsearch索引中删除商品，用于商品下架时同步数据
     * </p>
     *
     * @param itemId 商品ID
     */
    void deleteItemById(Long itemId);
}