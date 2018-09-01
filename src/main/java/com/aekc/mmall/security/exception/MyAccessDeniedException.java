package com.aekc.mmall.security.exception;

import org.springframework.security.access.AccessDeniedException;

public class MyAccessDeniedException extends AccessDeniedException {

    public MyAccessDeniedException(String msg) {
        super(msg);
    }
}
