package com.aekc.mmall.security.authorization;

import org.springframework.security.access.AccessDeniedException;

/**
 * 授权异常
 */
public class MyAccessDeniedException extends AccessDeniedException {

    public MyAccessDeniedException(String msg) {
        super(msg);
    }
}
