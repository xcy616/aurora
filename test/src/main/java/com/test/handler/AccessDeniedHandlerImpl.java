package com.test.handler;

import com.alibaba.fastjson.JSON;
import com.test.model.ResultVO;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 权限不足处理器
 *
 * 原项目路径：com.aurora.handler.AccessDeniedHandlerImpl
 * 触发条件：用户已登录（有 token），但角色不够（比如普通用户访问 admin 接口）
 *
 * 触发位置：ExceptionTranslationFilter 捕获到 AccessDeniedException 后调用
 * 返回：{"code":500, "msg":"权限不足"}
 */
@Component
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        System.out.println("[DeniedHandler] 权限不足，访问: " + request.getRequestURI());
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(JSON.toJSONString(ResultVO.fail("权限不足")));
    }
}
