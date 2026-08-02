package com.test.handler;

import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 访问决策管理器（比对用户角色和资源所需角色）
 *
 * 原项目路径：com.aurora.handler.AccessDecisionManagerImpl
 * 作用：检查当前用户的角色是否包含资源所需的角色
 *
 * 触发位置：FilterSecurityInterceptor 内部调用
 *   1. FilterSecurityInterceptor 先调 SecurityMetadataSource.getAttributes() 拿到所需角色
 *   2. 再调 AccessDecisionManager.decide() 比对用户角色
 *
 * 判断逻辑（"或"关系）：
 *   用户角色 ["user"] vs 所需角色 ["admin"] → 不包含 → 抛 AccessDeniedException
 *   用户角色 ["admin"] vs 所需角色 ["admin"] → 包含 → return 放行
 *   用户角色 ["admin","user"] vs 所需角色 ["user"] → 包含 → return 放行
 */
@Component
public class AccessDecisionManagerImpl implements AccessDecisionManager {

    @Override
    public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes)
            throws AccessDeniedException {

        // 第1步：从 SecurityContext 取出当前用户的角色列表
        List<String> userRoles = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        System.out.println("[AccessDecisionManager] 用户角色: " + userRoles);

        // 第2步：遍历资源所需角色，看用户有没有命中
        for (ConfigAttribute required : configAttributes) {
            if (userRoles.contains(required.getAttribute())) {
                System.out.println("[AccessDecisionManager] 用户拥有所需角色: " + required.getAttribute() + "，放行！");
                return;  // 命中任意一个就放行
            }
        }

        // 第3步：一个都没命中，抛异常
        System.out.println("[AccessDecisionManager] 用户角色不足，拒绝访问");
        throw new AccessDeniedException("权限不足");
    }

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return true;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }
}
