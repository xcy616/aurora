package com.aurora.annotation;

import java.lang.annotation.*;

/**
 * 接口访问限流注解
 *
 * 作用：标记在 Controller 方法上，声明该接口的限流规则
 * 消费方：AccessLimitInterceptor（拦截器）
 *
 * 工作流程：
 * 1. 拦截器在请求进入 Controller 前检查方法上是否有此注解
 * 2. 有注解 → 根据 IP + 方法名生成 Redis key，计数器+1
 * 3. 超过 maxCount → 拦截请求，返回"请求过于频繁"
 *
 * 用法示例：
 *   @AccessLimit(seconds = 60, maxCount = 5)
 *   @GetMapping("/articles")
 *   public ResultVO<?> listArticles() { ... }
 *   → 表示每个IP 60秒内最多请求5次
 */
@Target(ElementType.METHOD)     // 只能标在方法上
@Retention(RetentionPolicy.RUNTIME)  // 运行时保留（拦截器才能反射读取）
@Documented
public @interface AccessLimit {

    /**
     * 时间窗口（秒）：
     * 在这个时间范围内的请求会被计数
     */
    int seconds();

    /**
     * 最大请求次数：
     * 时间窗口内最多允许请求多少次，超过则拦截
     */
    int maxCount();
}
