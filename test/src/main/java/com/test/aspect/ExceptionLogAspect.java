package com.test.aspect;

import com.alibaba.fastjson.JSON;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 异常日志切面
 *
 * 原项目路径：com.aurora.aspect.ExceptionLogAspect
 *
 * 触发条件：Controller 层的方法抛出异常后（@AfterThrowing）
 * 不拦截异常（异常仍然会往外抛），只记录，用异步方式不影响主流程
 *
 * 工作流程：
 *   1. Controller 方法执行过程中抛出异常
 *   2. @AfterThrowing 触发 saveExceptionLog()
 *   3. 采集：方法名、请求参数、异常堆栈
 *   4. 异步记录（原项目写入数据库，这里打印到控制台）
 */
@Aspect
@Component
public class ExceptionLogAspect {

    /**
     * 切点：所有 Controller 层的方法
     */
    @Pointcut("execution(* com.test.controller..*.*(..))")
    public void exceptionLogPointcut() {}

    /**
     * 方法抛出异常后执行
     * throwing = "e" 把异常对象传进来
     */
    @AfterThrowing(value = "exceptionLogPointcut()", throwing = "e")
    @Async  // 异步执行，不阻塞主流程
    public void saveExceptionLog(JoinPoint joinPoint, Exception e) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        String className = joinPoint.getTarget().getClass().getName();
        String methodName = className + "." + method.getName();
        String requestParam = joinPoint.getArgs().length > 0 ? JSON.toJSONString(joinPoint.getArgs()) : "无";

        System.out.println("[ExceptionLog] ====================");
        System.out.println("[ExceptionLog] 异常方法: " + methodName);
        System.out.println("[ExceptionLog] 请求参数: " + requestParam);
        System.out.println("[ExceptionLog] 异常类型: " + e.getClass().getName());
        System.out.println("[ExceptionLog] 异常消息: " + e.getMessage());
        System.out.println("[ExceptionLog] ====================");
    }
}
