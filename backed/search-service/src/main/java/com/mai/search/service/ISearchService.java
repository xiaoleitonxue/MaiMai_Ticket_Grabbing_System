package com.mai.search.service;

import com.mai.api.dto.ItemDTO;
import com.mai.common.domain.PageDTO;
import com.mai.search.domain.dto.CategoryAndBrandDTO;
import com.mai.search.domain.po.ItemDoc;
import com.mai.search.domain.query.ItemPageQuery;

import java.util.List;
import java.util.Map;

public interface ISearchService {
    PageDTO<ItemDTO> search(ItemPageQuery query);

    CategoryAndBrandDTO filters(ItemPageQuery query);

    void saveItemById(Long itemId);

    void deleteItemById(Long itemId);
}