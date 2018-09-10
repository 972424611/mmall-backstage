package com.aekc.mmall.interceptor;

import com.aekc.mmall.common.ApplicationContextHelper;
import com.aekc.mmall.common.RequestHolder;
import com.aekc.mmall.enums.TokenState;
import com.aekc.mmall.exception.JwtException;
import com.aekc.mmall.model.SysUser;
import com.aekc.mmall.service.SysUserService;
import com.aekc.mmall.service.impl.SysUserServiceImpl;
import com.aekc.mmall.utils.JwtUtil;
import net.minidev.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtInterceptor.class);

    private void addRequestHolder(HttpServletRequest request) {
        JSONObject jsonObject = (JSONObject) request.getAttribute("data");
        String userId = jsonObject.getAsString("uid");
        SysUserService sysUserService = ApplicationContextHelper.getBean(SysUserServiceImpl.class);
        SysUser sysUser = sysUserService.findByUserId(Integer.valueOf(userId));
        RequestHolder.add(request);
        RequestHolder.add(sysUser);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 前段ajax自定义headers字段，会出现了option请求，在GET请求之前。
        // 所以应该把他过滤掉，以免影响服务。但是不能返回false，如果返回false会导致后续请求不会继续。
        if("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        //从请求头中获取token
        String token = request.getHeader("token");
        Map<String, Object> resultMap = JwtUtil.validToken(token);
        TokenState state = TokenState.getTokenState((String) resultMap.get("state"));
        switch(state) {
            case VALID:
                //　取出payload中数据，放到request作用域中
                request.setAttribute("data", resultMap.get("data"));
                addRequestHolder(request);
                return true;
            case EXPIRED:
            case INVALID:
                LOGGER.warn("无效token");
                throw new JwtException("您的token不合法或者过期了，请重新登陆");
            default:
                break;
        }
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        RequestHolder.clean();
    }
}
