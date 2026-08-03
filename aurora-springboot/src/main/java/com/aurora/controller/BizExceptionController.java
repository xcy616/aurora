package com.aurora.controller;

import com.aurora.exception.BizException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 异常转发控制器
 *
 * 作用：配合过滤器/拦截器使用——当业务代码中抛出的 BizException
 * 被过滤器捕获后，会通过 forward 转发到这个接口，把异常重新抛给
 * Spring MVC 的全局异常处理器（ControllerAdviceHandler）
 *
 * 场景：过滤器/拦截器阶段抛出的异常不会走 @RestControllerAdvice，
 * 所以用这个接口作为"中转站"，让异常回到 Spring MVC 流程中
 */
@Api(tags = "异常处理模块")
@RestController
public class BizExceptionController {

    @SneakyThrows
    @ApiOperation("/处理BizException")
    @RequestMapping("/bizException")
    public void handleBizException(HttpServletRequest request) {
        // 从请求属性中取出之前捕获的 BizException，重新抛出
        if (request.getAttribute("bizException") instanceof BizException) {
            System.out.println(request.getAttribute("bizException"));
            throw ((BizException) request.getAttribute("bizException"));
        } else {
            throw new Exception();
        }
    }

}
