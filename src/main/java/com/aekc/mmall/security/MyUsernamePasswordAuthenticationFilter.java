//package com.aekc.mmall.security;
//
//import org.apache.tomcat.util.http.fileupload.IOUtils;
//import org.springframework.security.authentication.AuthenticationServiceException;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
//import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
//import org.springframework.stereotype.Component;
//import org.springframework.stereotype.Service;
//
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.ServletRequest;
//import javax.servlet.ServletResponse;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.nio.Buffer;
//
//
//public class MyUsernamePasswordAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
//
//    public MyUsernamePasswordAuthenticationFilter() {
//        super(new AntPathRequestMatcher("/user/login", "POST"));
//    }
//
//    @Override
//    public Authentication attemptAuthentication(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws AuthenticationException, IOException {
//
//        BufferedReader bufferedReader = httpServletRequest.getReader();
//        String str;
//        while((str = bufferedReader.readLine()) != null) {
//            System.out.println(str);
//        }
//        return null;
//    }
//}
