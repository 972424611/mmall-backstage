package com.aekc.mmall.security;

import com.aekc.mmall.dao.SysRoleMapper;
import com.aekc.mmall.dao.SysRoleUserMapper;
import com.aekc.mmall.dao.SysUserMapper;
import com.aekc.mmall.model.SysAcl;
import com.aekc.mmall.model.SysRole;
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
public class CustomUserDetailService implements UserDetailsService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private SysRoleUserMapper sysRoleUserMapper;

    @Autowired
    private SysRoleMapper sysRoleMapper;

    /**
     * 登陆验证时，通过username获取用户的所有权限信息
     * 并返回User放到spring的全局缓存SecurityContextHolder中，以供授权器使用
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        CustomUserDetails customUserDetails = new CustomUserDetails();
        customUserDetails.setUsername(username);
        SysUser sysUser = sysUserMapper.selectByKeyword(username);
        List<Integer> roleIdList = sysRoleUserMapper.selectRoleIdListByUserId(sysUser.getId());

        List<SysRole> sysRoleList = new ArrayList<>();
        for(Integer roleId : roleIdList) {
            SysRole sysRole = sysRoleMapper.selectByPrimaryKey(roleId);
            sysRoleList.add(sysRole);
        }

        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();
        for(SysRole sysRole : sysRoleList) {
            authorityList.add(new SimpleGrantedAuthority(sysRole.getName()));
        }
        customUserDetails.setAuthorities(authorityList);
        customUserDetails.setPassword(sysUser.getPassword());
        return customUserDetails;
    }

    public CustomUserDetails loadUserByUserId(Integer userId) {
        CustomUserDetails customUserDetails = new CustomUserDetails();
        SysUser sysUser = sysUserMapper.selectByPrimaryKey(userId);
        List<Integer> roleIdList = sysRoleUserMapper.selectRoleIdListByUserId(sysUser.getId());

        List<SysRole> sysRoleList = new ArrayList<>();
        for(Integer roleId : roleIdList) {
            SysRole sysRole = sysRoleMapper.selectByPrimaryKey(roleId);
            sysRoleList.add(sysRole);
        }

        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();
        for(SysRole sysRole : sysRoleList) {
            authorityList.add(new SimpleGrantedAuthority(sysRole.getName()));
        }
        customUserDetails.setAuthorities(authorityList);
        customUserDetails.setSysUser(sysUser);
        return customUserDetails;
    }
}
