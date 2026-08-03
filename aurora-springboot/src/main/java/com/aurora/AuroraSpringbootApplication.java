package com.aurora;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * Aurora 博客系统启动类
 *
 * 项目后端入口：
 * - @SpringBootApplication：Spring Boot 启动注解（自动配置 + 组件扫描 + 配置类）
 * - @MapperScan：扫描 com.aurora.mapper 包下所有 Mapper 接口，注册为 MyBatis 的 Bean
 *
 * 启动流程：
 * 1. 加载 application.yml 配置文件
 * 2. 组件扫描：扫描 com.aurora 包下所有 @Component/@Service/@Controller/@Repository
 * 3. 自动配置：根据依赖自动配置 Spring MVC、MyBatis-Plus、Redis、RabbitMQ 等
 * 4. 执行所有 @Configuration 配置类（WebSecurityConfig、WebMvcConfig 等）
 * 5. 启动内嵌 Tomcat，监听 8080 端口
 */
@SpringBootApplication
@MapperScan("com.aurora.mapper")
public class AuroraSpringbootApplication {

    public static void main(String[] args) {
        // 启动 Spring Boot 应用
        SpringApplication.run(AuroraSpringbootApplication.class, args);
    }

    /**
     * 注册 RestTemplate Bean
     * 作用：发起 HTTP 请求的工具类（第三方登录时用 RestTemplate 调 QQ/Gitee 的接口换取 token）
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
