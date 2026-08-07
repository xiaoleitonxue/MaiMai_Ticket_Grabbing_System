package com.mai.api.fallback;

import com.mai.api.client.UserClient;
import com.mai.common.exception.BizIllegalException;
import org.springframework.cloud.openfeign.FallbackFactory;

/**
 * <p>
 * 用户服务Feign降级工厂，当远程调用失败时抛出业务异常以实现快速失败
 * </p>
 */
public class UserClientFallback implements FallbackFactory<UserClient> {

    /**
     * <p>
     * 创建带降级逻辑的UserClient代理，调用时抛出BizIllegalException
     * </p>
     *
     * @param cause 远程调用失败的异常原因
     * @return 降级后的UserClient实现
     */
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