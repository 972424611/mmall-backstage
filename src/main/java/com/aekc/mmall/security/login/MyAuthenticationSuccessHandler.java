package com.aekc.mmall.security.login;

import com.aekc.mmall.common.JsonData;
import com.aekc.mmall.dao.SysUserMapper;
import com.aekc.mmall.model.SysUser;
import com.aekc.mmall.utils.JsonUtil;
import com.aekc.mmall.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Objects;

@Component
public class MyAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        // 允许跨域
        httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
        // 允许自定义请求头token(允许head跨域)
        httpServletResponse.setHeader("Access-Control-Allow-Headers", "token, Accept, Origin, X-Requested-With, Content-Type, Last-Modified");
        SysUser user = sysUserMapper.selectByKeyword(httpServletRequest.getParameter("username"));
        String token = JwtUtil.createTokenByUserId(user.getId());
        PrintWriter writer = httpServletResponse.getWriter();
        String resultJson = JsonUtil.objectToJson(JsonData.success(token));
        assert resultJson != null;
        writer.write(resultJson);
        writer.flush();
        writer.close();
    }
}
