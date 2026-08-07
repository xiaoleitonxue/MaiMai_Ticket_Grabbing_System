package com.mai.api.client;

import com.mai.api.config.DefaultFeignConfig;
import com.mai.api.fallback.UserClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;

/**
 * <p>
 * 用户服务Feign客户端，提供用户余额扣减的远程调用接口
 * </p>
 */
@FeignClient(value = "user-service",fallbackFactory = UserClientFallback.class, configuration = DefaultFeignConfig.class)
public interface UserClient {

    /**
     * <p>
     * 扣减用户余额，需要校验支付密码
     * </p>
     *
     * @param pw 支付密码
     * @param amount 扣减金额
     */
    @PutMapping("/users/money/deduct")
    public void deductMoney(@RequestParam("pw") String pw,@RequestParam("amount") Integer amount);
}