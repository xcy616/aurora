package com.test.service;

import com.test.model.UserDetailsDTO;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户详情服务（内存版）
 *
 * 原项目路径：com.aurora.handler.UserDetailsServiceImpl
 * 作用：Spring Security 登录时调用这个类，根据用户名查出用户信息（含密码和角色）
 *
 * 原项目从数据库查，这里直接硬编码两个用户：
 *   admin / 123456  → 角色 [admin, user]
 *   user  / 123456  → 角色 [user]
 *
 * 流程：
 *   1. 用户 POST /users/login（username=admin&password=123456）
 *   2. UsernamePasswordAuthenticationFilter 接住
 *   3. 调用 AuthenticationManager → 调用 UserDetailsService.loadUserByUsername("admin")
 *   4. 这里返回 UserDetailsDTO（包含密码哈希和角色）
 *   5. AuthenticationManager 用 BCrypt 比对密码，通过则调用 AuthenticationSuccessHandler
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;
    private static final Map<String, UserDetailsDTO> USER_DB = new HashMap<>();

    public UserDetailsServiceImpl(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 应用启动后执行，初始化内存用户表
     * 用 BCrypt 加密密码（和原项目一样）
     */
    @PostConstruct
    public void init() {
        String encodedPwd = passwordEncoder.encode("123456");

        UserDetailsDTO admin = new UserDetailsDTO();
        admin.setId(1);
        admin.setUsername("admin");
        admin.setPassword(encodedPwd);
        admin.setNickname("管理员");
        admin.setRoleList(Arrays.asList("admin", "user"));
        USER_DB.put("admin", admin);

        UserDetailsDTO user = new UserDetailsDTO();
        user.setId(2);
        user.setUsername("user");
        user.setPassword(encodedPwd);
        user.setNickname("普通用户");
        user.setRoleList(Collections.singletonList("user"));
        USER_DB.put("user", user);

        System.out.println("[UserDetailsService] 内存用户表初始化完成: " + USER_DB.keySet());
    }

    /**
     * Spring Security 登录时调用这个方法
     * UsernamePasswordAuthenticationFilter → AuthenticationManager → loadUserByUsername
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetailsDTO user = USER_DB.get(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        System.out.println("[UserDetailsService] 查到用户: " + username + "，角色: " + user.getRoleList());
        return user;
    }
}
