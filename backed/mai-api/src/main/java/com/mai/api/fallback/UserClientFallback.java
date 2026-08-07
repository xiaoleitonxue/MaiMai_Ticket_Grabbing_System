package com.mai.api.fallback;

import com.mai.api.client.UserClient;
import com.mai.common.exception.BizIllegalException;
import org.springframework.cloud.openfeign.FallbackFactory;

public class UserClientFallback implements FallbackFactory<UserClient> {
    @Override
    public UserClient create(Throwable cause) {
        return new UserClient() {
            @Override
            public void deductMoney(String pw, Integer amount) {
                throw new BizIllegalException("调用扣减金额失败!", cause);
            }
        };
    }
}
