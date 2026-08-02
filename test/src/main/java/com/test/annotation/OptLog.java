package com.test.annotation;

import java.lang.annotation.*;

/**
 * 操作日志注解
 *
 * 原项目路径：com.aurora.annotation.OptLog
 * 作用：标记需要记录操作日志的方法
 *
 * 用法示例：
 *   @OptLog(optType = "新增")
 *   public ResultVO addArticle() { ... }
 *
 * 工作流程：
 *   1. OperationLogAspect 切面匹配 @OptLog 注解
 *   2. 方法成功返回后（@AfterReturning）触发日志记录
 *   3. 异步写入数据库（这里简化为打印到控制台）
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OptLog {

    /**
     * 操作类型，比如 "新增"、"修改"、"删除"
     */
    String optType() default "";
}
