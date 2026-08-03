package com.aurora.aspect;

import com.alibaba.fastjson.JSON;
import com.aurora.entity.ExceptionLog;
import com.aurora.event.ExceptionLogEvent;
import com.aurora.util.ExceptionUtil;
import com.aurora.util.IpUtil;
import io.swagger.annotations.ApiOperation;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
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
 * 异常日志切面（AOP）
 *
 * 作用：自动记录所有 Controller 方法抛出的异常
 * 触发时机：方法抛出异常后（@AfterThrowing）
 * 不拦截异常：异常仍然继续往外抛，只是顺带记录一份日志
 * 与 OperationLogAspect 的区别：这里不需要 @OptLog 注解，通过包路径匹配所有 Controller
 *
 * 工作流程：
 * 1. 切点匹配 com.aurora.controller 包下所有方法
 * 2. 方法抛异常后，采集：URI、方法名、参数、异常堆栈、IP
 * 3. 组装 ExceptionLog 实体
 * 4. 发布 ExceptionLogEvent 事件（异步监听器写入 t_exception_log 表）
 */
@Aspect
@Component
public class ExceptionLogAspect {

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * 切点：com.aurora.controller 包下的所有方法
     */
    @Pointcut("execution(* com.aurora.controller..*.*(..))")
    public void exceptionLogPointcut() {
    }

    /**
     * 方法抛出异常后执行，采集并发布异常日志事件
     * @param joinPoint 被切的方法
     * @param e         抛出的异常对象
     */
    @AfterThrowing(value = "exceptionLogPointcut()", throwing = "e")
    public void saveExceptionLog(JoinPoint joinPoint, Exception e) {
        // 获取当前请求对象
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = (HttpServletRequest) Objects.requireNonNull(requestAttributes).resolveReference(RequestAttributes.REFERENCE_REQUEST);

        // 创建异常日志实体
        ExceptionLog exceptionLog = new ExceptionLog();

        // 反射获取方法信息
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        ApiOperation apiOperation = method.getAnnotation(ApiOperation.class);   // @ApiOperation（操作描述）

        // 采集：请求URI
        exceptionLog.setOptUri(Objects.requireNonNull(request).getRequestURI());

        // 采集：完整的类名.方法名
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = method.getName();
        methodName = className + "." + methodName;
        exceptionLog.setOptMethod(methodName);
        exceptionLog.setRequestMethod(Objects.requireNonNull(request).getMethod());  // HTTP方法

        // 采集：请求参数（文件类型特殊处理）
        if (joinPoint.getArgs().length > 0) {
            if (joinPoint.getArgs()[0] instanceof MultipartFile) {
                exceptionLog.setRequestParam("file");
            } else {
                exceptionLog.setRequestParam(JSON.toJSONString(joinPoint.getArgs()));
            }
        }

        // 采集：操作描述（@ApiOperation 的值，可能没有）
        if (Objects.nonNull(apiOperation)) {
            exceptionLog.setOptDesc(apiOperation.value());
        } else {
            exceptionLog.setOptDesc("");
        }

        // 采集：异常堆栈信息（转为完整字符串）
        exceptionLog.setExceptionInfo(ExceptionUtil.getTrace(e));

        // 采集：IP 和归属地
        String ipAddress = IpUtil.getIpAddress(request);
        exceptionLog.setIpAddress(ipAddress);
        exceptionLog.setIpSource(IpUtil.getIpSource(ipAddress));

        // 发布事件，由监听器异步写入数据库（不阻塞当前请求）
        applicationContext.publishEvent(new ExceptionLogEvent(exceptionLog));
    }

}
