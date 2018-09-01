package com.aekc.mmall.security;

import com.aekc.mmall.dao.SysUserMapper;
import com.aekc.mmall.model.SysAcl;
import com.aekc.mmall.model.SysUser;
import com.aekc.mmall.service.SysCoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MyCustomUserService implements UserDetailsService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private SysCoreService sysCoreService;

    /**
     * 登陆验证时，通过username获取用户的所有权限信息
     * 并返回User放到spring的全局缓存SecurityContextHolder中，以供授权器使用
     */
    /*@Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        MyUserDetails myUserDetails = new MyUserDetails();
        myUserDetails.setUsername(username);
        SysUser sysUser = sysUserMapper.selectByKeyword(username);
        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();
        List<SysAcl> sysAclList = sysCoreService.getUserAclList(sysUser.getId());

        for(SysAcl sysAcl : sysAclList) {
            authorityList.add(new SimpleGrantedAuthority(sysAcl.getName()));
        }
        myUserDetails.setAuthorities(authorityList);
        myUserDetails.setPassword(sysUser.getPassword());
        return myUserDetails;
    }*/

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        MyUserDetails myUserDetails = new MyUserDetails();
        myUserDetails.setUsername(username);
        SysUser sysUser = sysUserMapper.selectByKeyword(username);
        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();
        authorityList.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        authorityList.add(new SimpleGrantedAuthority("ROLE_USER"));
        myUserDetails.setAuthorities(authorityList);
        myUserDetails.setPassword(sysUser.getPassword());
        return myUserDetails;
    }
}
