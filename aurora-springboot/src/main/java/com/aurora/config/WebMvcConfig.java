package com.aurora.config;


import com.aurora.interceptor.PaginationInterceptor;
import com.aurora.interceptor.AccessLimitInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置
 *
 * 作用：
 * 1. 注册自定义拦截器（分页、限流）
 * 2. 配置跨域（CORS）
 *
 * 拦截器执行位置：请求经过过滤器链 → DispatcherServlet → 拦截器链 → Controller
 * 执行顺序：先注册的先执行 preHandle，后注册的先执行 afterCompletion
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private PaginationInterceptor paginationInterceptor;      // 分页拦截器

    @Autowired
    private AccessLimitInterceptor accessLimitInterceptor;    // 限流拦截器

    /**
     * 跨域配置：允许所有来源、方法、请求头
     * 作用：前端（8082端口）直接请求后端（8081端口）时不报跨域错误
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowCredentials(true)   // 允许携带 Cookie
                .allowedHeaders("*")     // 允许所有请求头
                .allowedOrigins("*")     // 允许所有来源
                .allowedMethods("*");    // 允许所有 HTTP 方法
    }

    /**
     * 注册拦截器：先注册的先执行
     * 1. PaginationInterceptor：提取 current/size 分页参数存入 ThreadLocal
     * 2. AccessLimitInterceptor：检查 @AccessLimit 注解做接口限流
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(paginationInterceptor);
        registry.addInterceptor(accessLimitInterceptor);
    }

}
