package com.test.handler;

import com.alibaba.fastjson.JSON;
import com.test.model.ResultVO;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 未登录处理器
 *
 * 原项目路径：com.aurora.handler.AuthenticationEntryPointImpl
 * 触发条件：用户没有 token（SecurityContext 里没 Authentication），访问了需要权限的资源
 *
 * 触发位置：ExceptionTranslationFilter 捕获到 AuthenticationException 后调用
 * 返回：{"code":40001, "msg":"用户未登录"}
 */
@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        System.out.println("[EntryPoint] 用户未登录，访问: " + request.getRequestURI());
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(JSON.toJSONString(ResultVO.fail(40001, "用户未登录")));
    }
}
