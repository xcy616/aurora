package com.aurora.annotation;

import java.lang.annotation.*;

/**
 * 操作日志注解
 *
 * 作用：标记在 Controller 方法上，声明该操作需要记录操作日志
 * 消费方：OperationLogAspect（AOP 切面）
 *
 * 工作流程：
 * 1. 切面匹配所有带此注解的方法
 * 2. 方法成功返回后（@AfterReturning）触发日志采集
 * 3. 采集：模块名、操作类型、方法名、请求参数、响应数据、操作用户、IP
 * 4. 发布 OperationLogEvent 事件，异步写入 t_operation_log 表
 *
 * 用法示例：
 *   @OptLog(optType = OptTypeConstant.UPDATE)  // "修改"
 *   @PutMapping("/users/info")
 *   public ResultVO<?> updateUserInfo(...) { ... }
 *   → 这个操作会被记录到操作日志表
 */
@Target(ElementType.METHOD)     // 只能标在方法上
@Retention(RetentionPolicy.RUNTIME)  // 运行时保留（切面才能反射读取）
@Documented
public @interface OptLog {

    /**
     * 操作类型：
     * 对应 OptTypeConstant 常量，比如 "新增"、"修改"、"删除"
     */
    String optType() default "";
}
