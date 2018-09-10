package com.aekc.mmall.security;

import com.aekc.mmall.common.RequestHolder;
import com.aekc.mmall.enums.TokenState;
import com.aekc.mmall.security.authentication.MyAuthenticationException;
import com.aekc.mmall.utils.JwtUtil;
import net.minidev.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.SecurityMetadataSource;
import org.springframework.security.access.intercept.AbstractSecurityInterceptor;
import org.springframework.security.access.intercept.InterceptorStatusToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

/**
 * 资源管理拦截器AbstractSecurityInterceptor
 * @author Twilight
 */
@Component
public class JwtSecurityInterceptor extends AbstractSecurityInterceptor implements Filter {

    private static final String FILTER_APPLIED = "__spring_security_jwtSecurityInterceptor_filterApplied";

    @Autowired
    private FilterInvocationSecurityMetadataSource securityMetadataSource;

    @Autowired
    private CustomUserDetailService customUserDetailService;

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtSecurityInterceptor.class);

    private void addRequestHolder(HttpServletRequest request) {
        JSONObject jsonObject = (JSONObject) request.getAttribute("data");
        String userId = jsonObject.getAsString("uid");
        CustomUserDetails customUserDetails = customUserDetailService.loadUserByUserId(Integer.valueOf(userId));
        UsernamePasswordAuthenticationToken authentication
                = new UsernamePasswordAuthenticationToken(customUserDetails.getUsername(), null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        RequestHolder.add(customUserDetails.getSysUser());
        RequestHolder.add(request);
    }

    private void checkToken(HttpServletRequest request) {
        String token = request.getHeader("token");
        Map<String, Object> resultMap = JwtUtil.validToken(token);
        TokenState state = TokenState.getTokenState((String) resultMap.get("state"));
        switch(state) {
            case VALID:
                request.setAttribute("data", resultMap.get("data"));
                addRequestHolder(request);
                break;
            case EXPIRED:
            case INVALID:
                LOGGER.warn("无效token");
                // 这里抛出异常就会被MyAuthenticationEntryPoint自定义异常捕获
                throw new MyAuthenticationException("您的token不合法或者过期了，请重新登陆");
            default:
                break;
        }
    }

    /**
     * 登陆后，每次访问资源都通过这个拦截器拦截
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        // 防止同一个请求重复请求
        if(servletRequest.getAttribute(FILTER_APPLIED) != null) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        servletRequest.setAttribute(FILTER_APPLIED, true);
        // 忽略springSecurity框架自带的/error
        if(request.getRequestURL().toString().contains("/error")) {
            return;
        }
        FilterInvocation filterInvocation = new FilterInvocation(servletRequest, servletResponse, filterChain);
        // 忽略OPTIONS请求
        if("OPTIONS".equals(request.getMethod())) {
            //invoke(filterInvocation);
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        //校验token
        checkToken(request);
        invoke(filterInvocation);
    }

    /**
     * @param filterInvocation 里面有一个被拦截的url
     */
    private void invoke(FilterInvocation filterInvocation) throws IOException, ServletException {
        // 里面调用MyInvocationSecurityMetadataSource的getAttributes(Object object)这个方法获取filterInvocation对应的所有权限
        // 再调用MyAccessDecisionManager的decide方法来校验用户的权限是否足够
        InterceptorStatusToken statusToken = super.beforeInvocation(filterInvocation);
        try {
            // 执行下一个拦截器
            filterInvocation.getChain().doFilter(filterInvocation.getRequest(), filterInvocation.getResponse());
        } finally {
            super.afterInvocation(statusToken, null);
            // 清理Holder，(其实后面security也会帮我们清理)
            SecurityContextHolder.clearContext();
        }
    }

    @Autowired
    public void setMyAccessDecisionManager(PermissionCheck permissionCheck) {
        super.setAccessDecisionManager(permissionCheck);
    }

    @Override
    public Class<?> getSecureObjectClass() {
        return FilterInvocation.class;
    }

    @Override
    public SecurityMetadataSource obtainSecurityMetadataSource() {
        return this.securityMetadataSource;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }
}
