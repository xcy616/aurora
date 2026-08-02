package com.test.util;

import com.test.model.UserDetailsDTO;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * 用户工具类
 *
 * 原项目路径：com.aurora.util.UserUtil
 * 作用：从 SecurityContext 中取出当前登录用户信息
 *
 * 原理：JwtAuthenticationTokenFilter 在过滤器阶段把用户信息存入了 SecurityContext
 *       后续任何地方（Controller、Service、Aspect）都能通过这个工具类取到
 */
@Component
public class UserUtil {

    /**
     * 获取当前登录用户的完整信息
     */
    public static UserDetailsDTO getUserDetailsDTO() {
        return (UserDetailsDTO) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
    }

    /**
     * 获取当前 Authentication 对象（可能为 null，用于判断是否登录）
     */
    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
