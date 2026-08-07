package com.mai.api.fallback;

import com.mai.api.client.CartClient;
import com.mai.common.exception.BizIllegalException;
import org.springframework.cloud.openfeign.FallbackFactory;

import java.util.Collection;

/**
 * <p>
 * 购物车服务Feign降级工厂，当远程调用失败时抛出业务异常以实现快速失败
 * </p>
 */
public class CartClientFallback implements FallbackFactory<CartClient> {

    /**
     * <p>
     * 创建带降级逻辑的CartClient代理，调用时抛出BizIllegalException
     * </p>
     *
     * @param cause 远程调用失败的异常原因
     * @return 降级后的CartClient实现
     */
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