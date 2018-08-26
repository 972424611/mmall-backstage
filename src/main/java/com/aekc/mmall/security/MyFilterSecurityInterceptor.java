//package com.aekc.mmall.security;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.access.SecurityMetadataSource;
//import org.springframework.security.access.intercept.AbstractSecurityInterceptor;
//import org.springframework.security.access.intercept.InterceptorStatusToken;
//import org.springframework.security.web.FilterInvocation;
//import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
//import org.springframework.stereotype.Service;
//
//import javax.servlet.*;
//import javax.servlet.http.HttpServletRequest;
//import java.io.IOException;
//
///**
// * 资源管理拦截器AbstractSecurityInterceptor
// */
//@Service
//public class MyFilterSecurityInterceptor extends AbstractSecurityInterceptor implements Filter {
//
//    /**
//     * 配置文件注入
//     */
//    @Autowired
//    private FilterInvocationSecurityMetadataSource securityMetadataSource;
//
//    /**
//     * 登陆后，每次访问资源都通过这个拦截器拦截
//     */
//    @Override
//    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
//        HttpServletRequest requesst = (HttpServletRequest) servletRequest;
//        System.out.println(requesst.getMethod());
//        if("OPTIONS".equals(requesst.getMethod())) {
//            filterChain.doFilter(servletRequest, servletResponse);
//            return;
//        }
//        FilterInvocation filterInvocation = new FilterInvocation(servletRequest, servletResponse, filterChain);
//        invoke(filterInvocation);
//    }
//
//    /**
//     * @param filterInvocation 里面有一个被拦截的url
//     */
//    private void invoke(FilterInvocation filterInvocation) throws IOException, ServletException {
//        // 里面调用MyInvocationSecurityMetadataSource的getAttributes(Object object)这个方法获取filterInvocation对应的所有权限
//        // 再调用MyAccessDecisionManager的decide方法来校验用户的权限是否足够
//        InterceptorStatusToken statusToken = super.beforeInvocation(filterInvocation);
//        try {
//            // 执行下一个拦截器
//            filterInvocation.getChain().doFilter(filterInvocation.getRequest(), filterInvocation.getResponse());
//        } finally {
//            super.afterInvocation(statusToken, null);
//        }
//    }
//
//    @Autowired
//    public void setMyAccessDecisionManager(MyAccessDecisionManager myAccessDecisionManager) {
//        super.setAccessDecisionManager(myAccessDecisionManager);
//    }
//
//    @Override
//    public Class<?> getSecureObjectClass() {
//        return FilterInvocation.class;
//    }
//
//    @Override
//    public SecurityMetadataSource obtainSecurityMetadataSource() {
//        return this.securityMetadataSource;
//    }
//
//    @Override
//    public void init(FilterConfig filterConfig) throws ServletException {
//    }
//
//
//    @Override
//    public void destroy() {
//    }
//}
