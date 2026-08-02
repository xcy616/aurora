package com.test.annotation;

import java.lang.annotation.*;

/**
 * 接口限流注解
 *
 * 原项目路径：com.aurora.annotation.AccessLimit
 * 作用：标记需要限流的接口，指定时间窗口和最大请求次数
 *
 * 用法示例：
 *   @AccessLimit(seconds = 60, maxCount = 5)
 *   表示 60 秒内最多允许请求 5 次
 *
 * 工作流程：
 *   1. AccessLimitInterceptor.preHandle() 检查方法上有没有这个注解
 *   2. 有注解 → 根据 IP + 方法名 生成 key，用 Redis 计数
 *   3. 超过 maxCount → 拦截请求，返回"请求过于频繁"
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AccessLimit {

    /**
     * 时间窗口（秒）
     */
    int seconds();

    /**
     * 最大请求次数
     */
    int maxCount();
}
