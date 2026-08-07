package com.mai.api.client;

import com.mai.api.config.DefaultFeignConfig;
import com.mai.api.dto.PayOrderDTO;
import com.mai.api.fallback.PayClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "pay-service", fallbackFactory = PayClientFallback.class, configuration = DefaultFeignConfig.class)
public interface PayClient {

    /**
     * 根据业务订单号查询支付单
     * @param bizOrderNo 业务订单号
     * @return 支付单信息
     */
    @GetMapping("/pay-orders/biz/{bizOrderNo}")
    PayOrderDTO queryPayOrderByBizOrderNo(@PathVariable("bizOrderNo") Long bizOrderNo);
}
