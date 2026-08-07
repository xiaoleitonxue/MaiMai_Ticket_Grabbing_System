package com.mai.common.utils;

/**
 * <p>
 * 用户上下文工具类，基于ThreadLocal实现当前请求用户ID的线程隔离存取
 * </p>
 */
public class UserContext {
    private static final ThreadLocal<Long> tl = new ThreadLocal<>();

    /**
     * <p>
     * 将当前登录用户ID保存到ThreadLocal中，供当前线程后续使用
     * </p>
     *
     * @param userId 用户ID
     */
    public static void setUser(Long userId) {
        tl.set(userId);
    }

    /**
     * <p>
     * 获取当前线程中保存的登录用户ID
     * </p>
     *
     * @return 用户ID，如果未设置则返回null
     */
    public static Long getUser() {
        return tl.get();
    }

    /**
     * <p>
     * 移除当前线程中保存的用户信息，防止内存泄漏
     * </p>
     */
    public static void removeUser(){
        tl.remove();
    }
}