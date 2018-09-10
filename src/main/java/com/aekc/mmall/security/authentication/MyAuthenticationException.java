package com.aekc.mmall.security.authentication;

import org.springframework.security.core.AuthenticationException;

/**
 * 认证异常
 */
public class MyAuthenticationException extends AuthenticationException {

    public MyAuthenticationException(String msg) {
        super(msg);
    }
}
