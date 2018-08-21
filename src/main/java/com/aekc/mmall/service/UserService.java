package com.aekc.mmall.service;

import com.aekc.mmall.common.JsonData;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface UserService {

    void logout();

    JsonData login(HttpServletRequest request, HttpServletResponse response);
}
