package com.aekc.mmall.common;

import com.aekc.mmall.model.SysUser;

import javax.servlet.http.HttpServletRequest;

/**
 * 高并发下可以处理各自的数据，优化。
 */
public class RequestHolder {

    private static final ThreadLocal<SysUser> userHolder = new ThreadLocal<>();

    private static final ThreadLocal<HttpServletRequest> requestHolder = new ThreadLocal<>();

    public static void add(SysUser sysUser) {
        userHolder.set(sysUser);
    }

    public static void add(HttpServletRequest request) {
        requestHolder.set(request);
    }

    public static SysUser getCurrentUser() {
        return userHolder.get();
    }

    public static HttpServletRequest getCurrentRequest() {
        return requestHolder.get();
    }

    public static void remove() {
        userHolder.remove();
        requestHolder.remove();
    }
}
