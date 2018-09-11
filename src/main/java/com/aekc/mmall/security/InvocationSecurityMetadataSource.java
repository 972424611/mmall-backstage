package com.aekc.mmall.security;

import com.aekc.mmall.dao.SysAclMapper;
import com.aekc.mmall.model.SysAcl;
import com.aekc.mmall.model.SysRole;
import com.aekc.mmall.service.SysRoleService;
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
    private SysRoleService sysRoleService;

    private HashMap<String, Collection<ConfigAttribute>> authorizationMap = null;

    /**
     * 判断数据库中的权限是否改变。如果为true则需要重置map
     */
    public static boolean update = false;

    /**
     * 加载数据库中所有权限
     */
    private void loadResourceDefine() {
        authorizationMap = Maps.newHashMap();
        // 以url为key，value为请求该url所需要的角色
        List<SysAcl> sysAclList = sysAclMapper.selectAllAcl();
        for(SysAcl sysAcl : sysAclList) {
            if(sysAcl.getStatus() != 1) {
                // 权限点无效
                continue;
            }
            List<SysRole> sysRoleList = sysRoleService.getRoleListByAclId(sysAcl.getId());
            for(SysRole role : sysRoleList) {
                ConfigAttribute configAttribute = new SecurityConfig(role.getName());
                if(authorizationMap.get(sysAcl.getUrl()) != null) {
                    authorizationMap.get(sysAcl.getUrl()).add(configAttribute);
                } else {
                    List<ConfigAttribute> configAttributeList = new ArrayList<>();
                    configAttributeList.add(configAttribute);
                    authorizationMap.put(sysAcl.getUrl(), configAttributeList);
                }
            }
        }
        // 设置为false表示已经把map更新到最新
        update = false;
    }

    /**
     * 此方法是为了判定用户请求的url 是否在权限表中，如果在权限表中，
     * 则返回给 MyAccessDecisionManager的decide 方法，用来判定用户是否有此权限。如果不在权限表中则放行。
     * @param o 中包含用户请求的request 信息
     */
    @Override
    public Collection<ConfigAttribute> getAttributes(Object o) throws IllegalArgumentException {
        if(authorizationMap == null || update) {
            synchronized(InvocationSecurityMetadataSource.class) {
                if(authorizationMap == null || update) {
                    loadResourceDefine();
                }
            }
        }
        HttpServletRequest request = ((FilterInvocation) o).getHttpRequest();
        for(Map.Entry<String, Collection<ConfigAttribute>> entry : authorizationMap.entrySet()) {
            String resUrl = entry.getKey();
            AntPathRequestMatcher matcher = new AntPathRequestMatcher(resUrl);
            if(matcher.matches(request)) {
                return authorizationMap.get(resUrl);
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
