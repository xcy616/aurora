package com.test.aspect;

import com.alibaba.fastjson.JSON;
import com.test.annotation.OptLog;
import com.test.util.UserUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 操作日志切面
 *
 * 原项目路径：com.aurora.aspect.OperationLogAspect
 *
 * 触发条件：方法有 @OptLog 注解 且 方法成功返回后（@AfterReturning）
 * 不拦截请求，只记录，用异步方式不影响主流程
 *
 * 工作流程：
 *   1. Controller 方法执行成功
 *   2. @AfterReturning 触发 saveOperationLog()
 *   3. 采集：模块名、操作类型、方法名、请求参数、响应数据、用户信息
 *   4. 异步记录（原项目写入数据库，这里打印到控制台）
 *
 * 注解用法：
 *   @OptLog(optType = "新增")
 *   public ResultVO addArticle() { ... }
 */
@Aspect
@Component
public class OperationLogAspect {

    /**
     * 切点：匹配所有带 @OptLog 注解的方法
     */
    @Pointcut("@annotation(com.test.annotation.OptLog)")
    public void operationLogPointCut() {}

    /**
     * 方法成功返回后执行
     */
    @AfterReturning(value = "operationLogPointCut()", returning = "result")
    @Async  // 异步执行，不阻塞主流程
    public void saveOperationLog(JoinPoint joinPoint, Object result) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        OptLog optLog = method.getAnnotation(OptLog.class);

        // 采集信息
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = className + "." + method.getName();
        String requestParam = joinPoint.getArgs().length > 0 ? JSON.toJSONString(joinPoint.getArgs()) : "无";
        String responseData = JSON.toJSONString(result);

        System.out.println("[OperationLog] ====================");
        System.out.println("[OperationLog] 操作类型: " + optLog.optType());
        System.out.println("[OperationLog] 执行方法: " + methodName);
        System.out.println("[OperationLog] 请求参数: " + requestParam);
        System.out.println("[OperationLog] 响应结果: " + responseData);

        // 原项目这里还记录 userId、IP 等，通过 applicationContext.publishEvent 异步写数据库
        try {
            String user = UserUtil.getUserDetailsDTO().getUsername();
            System.out.println("[OperationLog] 操作用户: " + user);
        } catch (Exception e) {
            System.out.println("[OperationLog] 操作用户: 未登录");
        }
        System.out.println("[OperationLog] ====================");
    }
}
