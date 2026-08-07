package com.mai.common.interceptor;

import cn.hutool.core.util.StrUtil;
import com.mai.common.utils.UserContext;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UserInfoInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String userId = request.getHeader("user-info");

        if(StrUtil.isNotBlank(userId)){
            UserContext.setUser(Long.parseLong(userId));
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        UserContext.removeUser();
    }
}
