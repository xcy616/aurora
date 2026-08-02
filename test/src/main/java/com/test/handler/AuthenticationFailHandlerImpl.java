package com.test.handler;

import com.alibaba.fastjson.JSON;
import com.test.model.ResultVO;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 登录失败处理器
 *
 * 原项目路径：com.aurora.handler.AuthenticationFailHandlerImpl
 * 触发条件：用户 POST /users/login，账号密码验证失败
 *
 * 返回：{"code":500, "msg":"密码错误"/"用户不存在"}
 */
@Component
public class AuthenticationFailHandlerImpl implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException {
        System.out.println("[FailHandler] 登录失败: " + e.getMessage());
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(JSON.toJSONString(ResultVO.fail(e.getMessage())));
    }
}
