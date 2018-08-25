package com.aekc.mmall.security;

import com.aekc.mmall.dao.SysUserMapper;
import com.aekc.mmall.model.SysAcl;
import com.aekc.mmall.model.SysUser;
import com.aekc.mmall.service.SysCoreService;
import com.aekc.mmall.utils.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MyCustomUserService implements UserDetailsService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private SysCoreService sysCoreService;

    /**
     * 登陆验证时，通过username获取用户的所有权限信息
     * 并返回User放到spring的全局缓存SecurityContextHolder中，以供授权器使用
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser sysUser = sysUserMapper.selectByKeyword(username);
        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();
        List<SysAcl> sysAclList = sysCoreService.getUserAclList(sysUser.getId());
        for(SysAcl sysAcl : sysAclList) {
            authorityList.add(new SimpleGrantedAuthority(sysAcl.getName()));
        }
        sysUser.setPassword(SecurityUtil.encrypt(sysUser.getPassword()));
        return new User(sysUser.getUsername(), sysUser.getPassword(), authorityList);
    }
}
