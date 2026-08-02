package com.test.handler;

import com.alibaba.fastjson.JSON;
import com.test.model.ResultVO;
import com.test.model.UserDetailsDTO;
import com.test.service.TokenService;
import com.test.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 登录成功处理器
 *
 * 原项目路径：com.aurora.handler.AuthenticationSuccessHandlerImpl
 * 触发条件：用户 POST /users/login，账号密码验证通过
 *
 * 做了 3 件事：
 *   1. 调用 tokenService.createToken() 生成 JWT（同时把用户信息存入 Redis/内存）
 *   2. 返回 token + 用户信息给前端
 *   3. 异步更新用户登录IP和时间（这里简化为打印日志）
 */
@Component
public class AuthenticationSuccessHandlerImpl implements AuthenticationSuccessHandler {

    @Autowired
    private TokenService tokenService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        System.out.println("[SuccessHandler] 登录成功！");

        UserDetailsDTO userDetailsDTO = UserUtil.getUserDetailsDTO();

        // 第1步：生成 JWT token
        String token = "";
        if (Objects.nonNull(authentication)) {
            token = tokenService.createToken(userDetailsDTO);
        }

        // 第2步：组装返回数据
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("userId", userDetailsDTO.getId());
        data.put("username", userDetailsDTO.getUsername());
        data.put("nickname", userDetailsDTO.getNickname());
        data.put("roleList", userDetailsDTO.getRoleList());

        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(JSON.toJSONString(ResultVO.ok(data)));

        // 第3步：异步更新登录信息（原项目更新 IP 和最后登录时间，这里简化）
        updateLoginInfo(userDetailsDTO);
    }

    /**
     * 异步更新用户登录信息
     * 原项目用 @Async + UserAuthMapper.updateById()
     */
    public void updateLoginInfo(UserDetailsDTO userDetailsDTO) {
        System.out.println("[SuccessHandler] 异步更新登录信息: " + userDetailsDTO.getUsername());
    }
}
