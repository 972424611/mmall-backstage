//package com.aekc.mmall.security;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.*;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Component;
//
//@Component
//public class MyAuthenticationProvider implements AuthenticationProvider {
//
//    @Autowired
//    private  MyCustomUserService myCustomUserService;
//
//    @Override
//    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
//        UsernamePasswordAuthenticationToken token
//                = (UsernamePasswordAuthenticationToken) authentication;
//        String username = token.getName();
//        MyUserDetails myUserDetails = null;
//
//        if(username !=null) {
//            myUserDetails = (MyUserDetails) myCustomUserService.loadUserByUsername(username);
//        }
//        System.out.println("$$"+myUserDetails);
//
//        if(myUserDetails == null) {
//            throw new UsernameNotFoundException("用户名/密码无效");
//        }
//
//        else if (!myUserDetails.isEnabled()){
//            System.out.println("jinyong用户已被禁用");
//            throw new DisabledException("用户已被禁用");
//        }else if (!myUserDetails.isAccountNonExpired()) {
//            System.out.println("guoqi账号已过期");
//            throw new LockedException("账号已过期");
//        }else if (!myUserDetails.isAccountNonLocked()) {
//            System.out.println("suoding账号已被锁定");
//            throw new LockedException("账号已被锁定");
//        }else if (!myUserDetails.isCredentialsNonExpired()) {
//            System.out.println("pingzheng凭证已过期");
//            throw new LockedException("凭证已过期");
//        }
//
//        String password = myUserDetails.getPassword();
//        //与authentication里面的credentials相比较
////        if(!password.equals(token.getCredentials())) {
////            throw new BadCredentialsException("Invalid username/password");
////        }
//        //授权
//        return new UsernamePasswordAuthenticationToken(myUserDetails, password,myUserDetails.getAuthorities());
//    }
//
//    @Override
//    public boolean supports(Class<?> aClass) {
//        return UsernamePasswordAuthenticationToken.class.equals(aClass);
//    }
//}
