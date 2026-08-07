package com.mai.api.fallback;

import com.mai.api.client.TradeClient;
import com.mai.common.exception.BizIllegalException;
import org.springframework.cloud.openfeign.FallbackFactory;

public class TradeClientFallback implements FallbackFactory<TradeClient> {
    @Override
    public TradeClient create(Throwable cause) {
        return new TradeClient() {
            @Override
            public void markOrderPaySuccess(Long orderId) {
                throw new BizIllegalException("订单支付成功失败! orderId为：" + orderId, cause);
            }
        };
    }
}
