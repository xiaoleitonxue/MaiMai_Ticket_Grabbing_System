package com.mai.api.client;

import com.mai.api.config.DefaultFeignConfig;
import com.mai.api.dto.PayOrderDTO;
import com.mai.api.fallback.PayClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * <p>
 * 支付服务Feign客户端，提供支付单查询的远程调用接口
 * </p>
 */
@FeignClient(value = "pay-service", fallbackFactory = PayClientFallback.class, configuration = DefaultFeignConfig.class)
public interface PayClient {

    /**
     * <p>
     * 根据业务订单号查询支付单信息，用于确认支付状态
     * </p>
     *
     * @param bizOrderNo 业务订单号
     * @return 支付单信息
     */
    @GetMapping("/pay-orders/biz/{bizOrderNo}")
    PayOrderDTO queryPayOrderByBizOrderNo(@PathVariable("bizOrderNo") Long bizOrderNo);
}