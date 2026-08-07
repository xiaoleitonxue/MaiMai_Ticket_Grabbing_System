package com.mai.api.fallback;

import com.mai.api.client.PayClient;
import com.mai.api.dto.PayOrderDTO;
import com.mai.common.exception.BizIllegalException;
import org.springframework.cloud.openfeign.FallbackFactory;

public class PayClientFallback implements FallbackFactory<PayClient> {
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
