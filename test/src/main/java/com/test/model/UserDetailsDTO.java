package com.test.model;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户详情（存入 SecurityContext 的对象）
 *
 * 原项目路径：com.aurora.model.dto.UserDetailsDTO
 * 作用：实现 UserDetails 接口，包含用户信息和角色列表
 *       JWT 只存 userId，用户完整信息存在 Redis，这个类就是从 Redis 取出来的完整信息
 */
@Data
public class UserDetailsDTO implements UserDetails {

    private Integer id;
    private String username;
    private String password;
    private String nickname;

    /**
     * 角色列表，比如 ["admin"] 或 ["user"]
     * Spring Security 用这个做权限判断
     */
    private List<String> roleList;

    /**
     * 把 roleList 转成 GrantedAuthority 集合
     * Spring Security 内部比对权限时调用这个方法
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roleList.stream().map(role -> (GrantedAuthority) () -> role).collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}
