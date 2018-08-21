package com.aekc.mmall.controller;

import com.aekc.mmall.common.JsonData;
import com.aekc.mmall.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping(value = "/user")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping("/logout.page")
    public void logout(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        request.getSession().invalidate();
        String path = "signin.jsp";
        response.sendRedirect(path);
    }

    @GetMapping(value = "/login")
    public JsonData login(HttpServletRequest request, HttpServletResponse response) {
        //
        return userService.login(request, response);
    }
}
