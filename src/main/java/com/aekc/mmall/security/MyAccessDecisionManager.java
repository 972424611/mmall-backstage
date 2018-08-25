package com.aekc.mmall.security;

import com.aekc.mmall.exception.CustomException;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 * 执行顺序
 * MyFilterSecurityInterceptor -> MyInvocationSecurityMetadataSource -> MyAccessDecisionManager
 *
 * 控制访问权限
 */
@Service
public class MyAccessDecisionManager implements AccessDecisionManager {

    /**
     * 方法是判定是否拥有权限的决策方法，
     * @param authentication 是释CustomUserService中循环添加到 GrantedAuthority 对象中的权限信息集合.
     * @param o 包含客户端发起的请求的requset信息，可转换为 HttpServletRequest request = ((FilterInvocation) object).getHttpRequest();
     * @param collection 为MyInvocationSecurityMetadataSource的getAttributes(Object object)这个方法返回的结果，此方法是为了判定用户请求的url 是否在权限表中，如果在权限表中，则返回给 decide 方法，用来判定用户是否有此权限。如果不在权限表中则放行。
     */
    @Override
    public void decide(Authentication authentication, Object o, Collection<ConfigAttribute> collection) throws AccessDeniedException, InsufficientAuthenticationException {
        if(CollectionUtils.isEmpty(collection)) {
            return;
        }
        for (ConfigAttribute configuration : collection) {
            String needAcl = configuration.getAttribute();
            for (GrantedAuthority authority : authentication.getAuthorities()) {
                if (needAcl.equals(authority.getAuthority())) {
                    return;
                }
            }
        }
        throw new CustomException("没有访问权限, 如需访问请联系管理员");
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
