package com.mai.api.fallback;

import com.mai.api.client.ItemClient;
import com.mai.api.dto.ItemDTO;
import com.mai.api.dto.OrderDetailDTO;
import com.mai.common.exception.BizIllegalException;
import com.mai.common.utils.CollUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

import java.util.Collection;
import java.util.List;

@Slf4j
public class ItemClientFallback implements FallbackFactory<ItemClient> {
    @Override
    public ItemClient create(Throwable cause) {
        return new ItemClient() {
            @Override
            public List<ItemDTO> queryItemsByIds(Collection<Long> ids) {
                log.error("调用商品id集合查询商品列表失败；具体参数为：{}", ids, cause);
                return CollUtils.emptyList();
            }

            @Override
            public void deductStock(List<OrderDetailDTO> items) {
                log.error("调用扣减库存失败；具体参数为：{}", items, cause);
                throw new BizIllegalException("调用扣减库存失败");
            }

            @Override
            public ItemDTO queryItemById(Long itemId) {
                log.error("调用商品id查询商品详情失败；具体参数为：{}", itemId, cause);
                return null;
            }

        };
    }
}
