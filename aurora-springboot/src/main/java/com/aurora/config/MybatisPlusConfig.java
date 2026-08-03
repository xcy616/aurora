package com.aurora.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * MyBatis-Plus 配置
 *
 * 作用：
 * 1. @EnableTransactionManagement：开启声明式事务（@Transactional 注解生效）
 * 2. 注册分页插件：让 MyBatis-Plus 的 Page 分页查询自动生成 LIMIT 语句
 *
 * 配合：PaginationInterceptor（请求层拦截器）把 current/size 存入 ThreadLocal，
 * Service 层用 PageUtil.getCurrentPage() 构造 Page 对象，分页插件自动拼 LIMIT
 */
@EnableTransactionManagement
@Configuration
public class MybatisPlusConfig {

    /**
     * 注册 MyBatis-Plus 拦截器（分页插件）
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));  // MySQL分页方言
        return interceptor;
    }

}