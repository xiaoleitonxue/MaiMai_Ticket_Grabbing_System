package com.mai.api.fallback;

import com.mai.api.client.TradeClient;
import com.mai.common.exception.BizIllegalException;
import org.springframework.cloud.openfeign.FallbackFactory;

/**
 * <p>
 * 交易服务Feign降级工厂，当远程调用失败时抛出业务异常以实现快速失败
 * </p>
 */
public class TradeClientFallback implements FallbackFactory<TradeClient> {

    /**
     * <p>
     * 创建带降级逻辑的TradeClient代理，调用时抛出BizIllegalException
     * </p>
     *
     * @param cause 远程调用失败的异常原因
     * @return 降级后的TradeClient实现
     */
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