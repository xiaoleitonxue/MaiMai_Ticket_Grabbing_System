package com.mai.common.interceptor;

import cn.hutool.core.util.StrUtil;
import com.mai.common.utils.UserContext;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * 用户信息拦截器，在请求处理前从请求头中提取用户ID并存入ThreadLocal，请求完成后自动清理
 * </p>
 */
public class UserInfoInterceptor implements HandlerInterceptor {

    /**
     * <p>
     * 请求预处理，从请求头中获取user-info并存入UserContext
     * </p>
     *
     * @param request HTTP请求
     * @param response HTTP响应
     * @param handler 处理器
     * @return true表示继续处理请求
     * @throws Exception 处理异常
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String userId = request.getHeader("user-info");

        if(StrUtil.isNotBlank(userId)){
            UserContext.setUser(Long.parseLong(userId));
        }

        return true;
    }

    /**
     * <p>
     * 请求完成后清理UserContext中的用户信息，防止内存泄漏
     * </p>
     *
     * @param request HTTP请求
     * @param response HTTP响应
     * @param handler 处理器
     * @param ex 异常，可能为null
     * @throws Exception 处理异常
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        UserContext.removeUser();
    }
}