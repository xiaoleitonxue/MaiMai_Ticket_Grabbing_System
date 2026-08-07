package com.mai.api.fallback;

import com.mai.api.client.CartClient;
import com.mai.common.exception.BizIllegalException;
import org.springframework.cloud.openfeign.FallbackFactory;

import java.util.Collection;

public class CartClientFallback implements FallbackFactory<CartClient> {

    @Override
    public CartClient create(Throwable cause) {
        return new CartClient() {

            @Override
            public void removeByItemIds(Collection<Long> ids) {
                throw new BizIllegalException("删除购物车商品失败! ids为：" + ids, cause);
            }
        };
    }
}
