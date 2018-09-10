package com.aekc.mmall.security;

import com.aekc.mmall.security.authentication.MyAuthenticationException;
import com.aekc.mmall.security.authorization.MyAccessDeniedException;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 执行顺序
 * MyFilterSecurityInterceptor -> MyInvocationSecurityMetadataSource -> MyAccessDecisionManager
 *
 * 控制访问权限
 */
@Component
public class PermissionCheck implements AccessDecisionManager {

    /**
     * 方法是判定是否拥有权限的决策方法，
     * @param authentication 用户拥有的角色
     * @param o 包含客户端发起的请求的requset信息，可转换为 HttpServletRequest request = ((FilterInvocation) object).getHttpRequest();
     * @param collection 该请求所需要的角色
     */
    @Override
    public void decide(Authentication authentication, Object o, Collection<ConfigAttribute> collection) {

        if(CollectionUtils.isEmpty(collection)) {
            return;
        }
        // 如果有交集
        if(collection.removeAll(authentication.getAuthorities())) {
            return;
        }
        throw new MyAccessDeniedException("权限不足");
    }

    @Override
    public boolean supports(ConfigAttribute configAttribute) {
        return true;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }
}
