package com.aekc.mmall.security;

import com.aekc.mmall.dao.SysAclMapper;
import com.aekc.mmall.dao.SysRoleAclMapper;
import com.aekc.mmall.dao.SysRoleMapper;
import com.aekc.mmall.model.SysAcl;
import com.aekc.mmall.model.SysRole;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 读取url资源
 */
@Component
public class InvocationSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {

    @Autowired
    private SysAclMapper sysAclMapper;

    @Autowired
    private SysRoleAclMapper sysRoleAclMapper;

    @Autowired
    private SysRoleMapper sysRoleMapper;

    private HashMap<String, Collection<ConfigAttribute>> map = null;

    /**
     * 加载数据库中所有权限
     */
    private void loadResourceDefine() {
        map = Maps.newHashMap();
        List<SysAcl> sysAclList = sysAclMapper.selectAllAcl();
        for(SysAcl sysAcl : sysAclList) {
            List<Integer> roleIdList = sysRoleAclMapper.selectRoleIdListByAclId(sysAcl.getId());

            List<SysRole> sysRoleList = new ArrayList<>();
            for(Integer roleId : roleIdList) {
                SysRole sysRole = sysRoleMapper.selectByPrimaryKey(roleId);
                sysRoleList.add(sysRole);
            }

            for(SysRole role : sysRoleList) {
                ConfigAttribute configAttribute = new SecurityConfig(role.getName());
                if(map.get(sysAcl.getUrl()) != null) {
                    map.get(sysAcl.getUrl()).add(configAttribute);
                } else {
                    List<ConfigAttribute> configAttributeList = new ArrayList<>();
                    configAttributeList.add(configAttribute);
                    map.put(sysAcl.getUrl(), configAttributeList);
                }
            }
        }
    }

    /**
     * 此方法是为了判定用户请求的url 是否在权限表中，如果在权限表中，
     * 则返回给 MyAccessDecisionManager的decide 方法，用来判定用户是否有此权限。如果不在权限表中则放行。
     * @param o 中包含用户请求的request 信息
     */
    @Override
    public Collection<ConfigAttribute> getAttributes(Object o) throws IllegalArgumentException {
        if(map == null) {
            loadResourceDefine();
        }
        HttpServletRequest request = ((FilterInvocation) o).getHttpRequest();
        for(Map.Entry<String, Collection<ConfigAttribute>> entry : map.entrySet()) {
            String resUrl = entry.getKey();
            AntPathRequestMatcher matcher = new AntPathRequestMatcher(resUrl);
            if(matcher.matches(request)) {
                return map.get(resUrl);
            }
        }
        // 表示请求该url不需要权限
        return null;
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }
}
