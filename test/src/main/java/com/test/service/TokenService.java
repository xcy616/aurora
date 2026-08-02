package com.test.service;

import com.test.model.UserDetailsDTO;

import javax.servlet.http.HttpServletRequest;

/**
 * Token 服务接口
 *
 * 原项目路径：com.aurora.service.TokenService
 * 作用：JWT token 的创建、解析、续期，以及从请求中提取用户信息
 */
public interface TokenService {

    /**
     * 创建 token（登录成功后调用）
     */
    String createToken(UserDetailsDTO userDetailsDTO);

    /**
     * 从请求头解析 token，查出用户信息（每个请求都调用）
     */
    UserDetailsDTO getUserDetailDTO(HttpServletRequest request);

    /**
     * 续期 token（快过期时重新刷新过期时间）
     */
    void renewToken(UserDetailsDTO userDetailsDTO);
}
