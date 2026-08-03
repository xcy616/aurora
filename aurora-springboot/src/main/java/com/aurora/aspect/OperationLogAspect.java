package com.aurora.aspect;

import com.alibaba.fastjson.JSON;
import com.aurora.annotation.OptLog;
import com.aurora.entity.OperationLog;
import com.aurora.event.OperationLogEvent;
import com.aurora.util.IpUtil;
import com.aurora.util.UserUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * 操作日志切面（AOP）
 *
 * 作用：自动记录所有带 @OptLog 注解的方法的操作日志
 * 触发时机：方法执行成功返回后（@AfterReturning）
 * 不侵入业务代码：日志逻辑全部集中在这里，Controller 只需加一个注解
 *
 * 工作流程：
 * 1. 切点匹配所有带 @OptLog 注解的方法
 * 2. 方法成功返回后，采集：模块名、操作类型、方法名、参数、响应、用户、IP
 * 3. 组装 OperationLog 实体
 * 4. 发布 OperationLogEvent 事件（异步监听器写入 t_operation_log 表）
 */
@Aspect
@Component
public class OperationLogAspect {

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * 切点：匹配所有带 @OptLog 注解的方法
     */
    @Pointcut("@annotation(com.aurora.annotation.OptLog)")
    public void operationLogPointCut() {
    }

    /**
     * 方法成功返回后执行，采集并发布操作日志事件
     * @param joinPoint 被切的方法（能拿到方法信息、参数、目标类）
     * @param keys      方法返回值
     */
    @AfterReturning(value = "operationLogPointCut()", returning = "keys")
    @SuppressWarnings("unchecked")
    public void saveOperationLog(JoinPoint joinPoint, Object keys) {
        // 从 Spring 的 RequestContextHolder 获取当前请求对象
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = (HttpServletRequest) Objects.requireNonNull(requestAttributes).resolveReference(RequestAttributes.REFERENCE_REQUEST);

        // 创建操作日志实体
        OperationLog operationLog = new OperationLog();

        // 反射获取：方法签名、方法、类上的注解
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Api api = (Api) signature.getDeclaringType().getAnnotation(Api.class);        // 类上的 @Api（模块名）
        ApiOperation apiOperation = method.getAnnotation(ApiOperation.class);        // 方法上的 @ApiOperation（操作描述）
        OptLog optLog = method.getAnnotation(OptLog.class);                          // 方法上的 @OptLog（操作类型）

        // 采集：模块名（来自 @Api 的 tags）、操作类型、操作描述
        operationLog.setOptModule(api.tags()[0]);
        operationLog.setOptType(optLog.optType());
        operationLog.setOptDesc(apiOperation.value());

        // 采集：完整的类名.方法名（如 com.aurora.controller.UserInfoController.updateUserInfo）
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = method.getName();
        methodName = className + "." + methodName;
        operationLog.setRequestMethod(Objects.requireNonNull(request).getMethod());  // HTTP方法（GET/POST...）
        operationLog.setOptMethod(methodName);                                      // 类名.方法名

        // 采集：请求参数（文件类型特殊处理，不转JSON）
        if (joinPoint.getArgs().length > 0) {
            if (joinPoint.getArgs()[0] instanceof MultipartFile) {
                operationLog.setRequestParam("file");
            } else {
                operationLog.setRequestParam(JSON.toJSONString(joinPoint.getArgs()));
            }
        }

        // 采集：响应数据、操作用户信息
        operationLog.setResponseData(JSON.toJSONString(keys));
        operationLog.setUserId(UserUtil.getUserDetailsDTO().getId());
        operationLog.setNickname(UserUtil.getUserDetailsDTO().getNickname());

        // 采集：IP 地址和 IP 归属地
        String ipAddress = IpUtil.getIpAddress(request);
        operationLog.setIpAddress(ipAddress);
        operationLog.setIpSource(IpUtil.getIpSource(ipAddress));

        // 采集：请求 URI
        operationLog.setOptUri(request.getRequestURI());

        // 发布事件，由监听器异步写入数据库（不阻塞当前请求）
        applicationContext.publishEvent(new OperationLogEvent(operationLog));
    }

}
