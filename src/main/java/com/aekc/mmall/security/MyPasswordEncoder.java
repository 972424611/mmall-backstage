//package com.aekc.mmall.security;
//
//import com.aekc.mmall.utils.SecurityUtil;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//
//@Component
//public class MyPasswordEncoder implements PasswordEncoder {
//
//    @Override
//    public String encode(CharSequence charSequence) {
//        return SecurityUtil.encrypt(charSequence.toString());
//    }
//
//    @Override
//    public boolean matches(CharSequence charSequence, String s) {
//        return encode(charSequence).equals(s);
//    }
//}