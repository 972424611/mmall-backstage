package com.aekc.mmall.interceptor;

import com.aekc.mmall.common.ApplicationContextHelper;
import com.aekc.mmall.exception.AclControlException;
import com.aekc.mmall.service.SysCoreService;
import com.aekc.mmall.service.impl.SysCoreServiceImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AclControlInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 前段ajax自定义headers字段，会出现了option请求，在GET请求之前。
        // 所以应该把他过滤掉，以免影响服务。但是不能返回false，如果返回false会导致后续请求不会继续。
        if("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String servletPath = request.getServletPath();
        SysCoreService sysCoreService = ApplicationContextHelper.getBean(SysCoreServiceImpl.class);
        if(!sysCoreService.hasUrlAcl(servletPath)) {
            throw new AclControlException("没有访问权限, 如需访问请联系管理员");
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
