package com.aekc.mmall.security.authentication;

import com.aekc.mmall.dao.SysUserMapper;
import com.aekc.mmall.model.SysUser;
import com.aekc.mmall.utils.JwtUtil;
import com.aekc.mmall.utils.ReturnUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 自定义登录成功后去向
 */
@Component
public class MyAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) {
        String username = httpServletRequest.getParameter("username");
        SysUser user = sysUserMapper.selectByKeyword(username);
        String token = JwtUtil.createTokenByUserId(user.getId());
        ReturnUtil.success(httpServletResponse, token);
    }
}
