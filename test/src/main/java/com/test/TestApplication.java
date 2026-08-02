package com.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 启动类
 *
 * 访问测试：
 * 1. 登录：POST http://localhost:8080/users/login  Body: username=admin&password=123456
 * 2. 返回 token 后，访问受保护接口：GET http://localhost:8080/admin/hello  Header: Authorization: Bearer <token>
 * 3. 普通用户登录：POST /users/login  Body: username=user&password=123456
 * 4. 普通用户访问 /admin/hello 会返回"权限不足"
 */
@SpringBootApplication
@EnableAsync  // 开启异步支持（操作日志异步写入用）
public class TestApplication {
    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }
}
