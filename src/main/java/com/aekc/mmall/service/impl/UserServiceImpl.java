package com.aekc.mmall.service.impl;

import com.aekc.mmall.common.JsonData;
import com.aekc.mmall.common.JsonDataRet;
import com.aekc.mmall.model.SysUser;
import com.aekc.mmall.service.SysUserService;
import com.aekc.mmall.service.UserService;
import com.aekc.mmall.utils.JwtUtil;
import com.aekc.mmall.utils.SecurityUtil;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private SysUserService sysUserService;

    @Override
    public void logout() {

    }

    @Override
    public JsonData login(HttpServletRequest request, HttpServletResponse response) {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        SysUser sysUser = sysUserService.findByKeyword(username);
        String errorMsg;
        String retPage = request.getParameter("ret");

        if (StringUtils.isBlank(username)) {
            errorMsg = "用户名不可以为空";
        } else if (StringUtils.isBlank(password)) {
            errorMsg = "密码不可以为空";
        } else if (sysUser == null) {
            errorMsg = "查询不到指定用户";
        } else if (!sysUser.getPassword().equals(SecurityUtil.encrypt(password))) {
            errorMsg = "用户名或密码错误";
        } else if (sysUser.getStatus() != 1) {
            errorMsg = "用户已被冻结, 请联系管理员";
        } else {
            // login success
            sysUser.setPassword(null);
            String token = JwtUtil.createTokenByUserId(sysUser.getId());
            if (StringUtils.isNotBlank(retPage)) {
                return JsonDataRet.success(token, retPage);
            } else {
                return JsonData.success(token);
            }
        }
        return JsonData.fail(errorMsg);
    }


}
