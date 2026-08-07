package com.mai.api.client;

import com.mai.api.config.DefaultFeignConfig;
import com.mai.api.fallback.UserClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;

@FeignClient(value = "user-service",fallbackFactory = UserClientFallback.class, configuration = DefaultFeignConfig.class)
public interface UserClient {

    @PutMapping("/users/money/deduct")
    public void deductMoney(@RequestParam("pw") String pw,@RequestParam("amount") Integer amount);
}
