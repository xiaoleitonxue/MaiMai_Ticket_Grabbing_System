package com.mai.api.fallback;

import com.mai.api.client.PayClient;
import com.mai.api.dto.PayOrderDTO;
import com.mai.common.exception.BizIllegalException;
import org.springframework.cloud.openfeign.FallbackFactory;

/**
 * <p>
 * 支付服务Feign降级工厂，当远程调用失败时抛出业务异常以实现快速失败
 * </p>
 */
public class PayClientFallback implements FallbackFactory<PayClient> {

    /**
     * <p>
     * 创建带降级逻辑的PayClient代理，调用时抛出BizIllegalException
     * </p>
     *
     * @param cause 远程调用失败的异常原因
     * @return 降级后的PayClient实现
     */
    @Override
    public PayClient create(Throwable cause) {
        return new PayClient() {
            @Override
            public PayOrderDTO queryPayOrderByBizOrderNo(Long bizOrderNo) {
                throw new BizIllegalException("查询支付单失败! bizOrderNo为：" + bizOrderNo, cause);
            }
        };
    }
}