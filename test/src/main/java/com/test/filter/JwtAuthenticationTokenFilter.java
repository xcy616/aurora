package com.test.filter;

import com.test.model.UserDetailsDTO;
import com.test.service.TokenService;
import com.test.util.UserUtil;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * JWT 认证过滤器（每个请求都经过）
 *
 * 原项目路径：com.aurora.filter.JwtAuthenticationTokenFilter
 * 作用：从请求头取出 JWT → 解析出用户信息 → 存入 SecurityContext
 *
 * 这是过滤器链的第一关，核心逻辑就 3 步：
 *   1. tokenService.getUserDetailDTO(request) → Header 取 JWT → 解析 userId → 从 Redis/内存 查出用户信息
 *   2. 如果用户信息存在且 SecurityContext 为空 → 存进去
 *   3. 无论有没有 token 都继续放行（权限判断交给后面的 FilterSecurityInterceptor）
 */
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;

    @SneakyThrows
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        System.out.println("\n[JwtFilter] 请求进入: " + request.getMethod() + " " + request.getRequestURI());

        // 第1步：从请求头取 JWT，解析出用户信息
        UserDetailsDTO userDetailsDTO = tokenService.getUserDetailDTO(request);

        // 第2步：用户信息存在 且 SecurityContext 里还没存过
        if (Objects.nonNull(userDetailsDTO) && Objects.isNull(UserUtil.getAuthentication())) {
            // 续期 token（原项目检查快过期了才续，这里简化为每次都续）
            tokenService.renewToken(userDetailsDTO);
            // 包成 Authentication 对象，存入 SecurityContext
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(userDetailsDTO, null, userDetailsDTO.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            System.out.println("[JwtFilter] 用户已认证: " + userDetailsDTO.getUsername() + "，角色: " + userDetailsDTO.getRoleList());
        } else {
            System.out.println("[JwtFilter] 无 token 或已认证，直接放行");
        }

        // 第3步：继续过滤器链
        filterChain.doFilter(request, response);
    }
}
