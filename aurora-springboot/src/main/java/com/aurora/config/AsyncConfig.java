package com.aurora.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 异步线程池配置
 *
 * 作用：配置 @Async 注解使用的线程池
 * 消费方：AuthenticationSuccessHandlerImpl.updateUserInfo()、
 *        OperationLogAspect.saveOperationLog()、ExceptionLogAspect 等异步方法
 *
 * 为什么需要：操作日志、登录信息更新等操作不紧急，
 * 用异步线程执行可以让用户更快收到响应（主线程不等异步任务）
 */
@EnableAsync
@Configuration
public class AsyncConfig {

    /**
     * 定义异步任务线程池
     * 核心线程10个，最大20个，队列20个，超过就丢弃排队等待
     */
    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);      // 核心线程数（常驻）
        executor.setMaxPoolSize(20);       // 最大线程数
        executor.setQueueCapacity(20);     // 任务队列容量
        executor.setKeepAliveSeconds(60);  // 空闲线程存活时间（秒）
        executor.setThreadNamePrefix("async-task-thread-");  // 线程名前缀（方便排查日志）
        return executor;
    }
}