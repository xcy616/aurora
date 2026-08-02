package com.test.config;

import com.test.interceptor.PaginationInterceptor;
import com.test.interceptor.AccessLimitInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置（注册拦截器）
 *
 * 原项目路径：com.aurora.config.WebMvcConfig
 *
 * 作用：把自定义的拦截器注册到 Spring MVC 的拦截器链中
 *       请求经过过滤器链后，由 DispatcherServlet 分发，然后进入拦截器链
 *
 * 执行顺序：先注册的先执行
 *   PaginationInterceptor（提取分页参数）→ AccessLimitInterceptor（限流检查）
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private PaginationInterceptor paginationInterceptor;

    @Autowired
    private AccessLimitInterceptor accessLimitInterceptor;

    /**
     * 解决跨域（devServer代理已经处理过，这里双保险）
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowCredentials(true)
                .allowedHeaders("*")
                .allowedOrigins("*")
                .allowedMethods("*");
    }

    /**
     * 注册拦截器
     * 先注册的先执行，afterCompletion 反过来（后注册的先执行）
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        System.out.println("[WebMvcConfig] 注册拦截器: PaginationInterceptor 和 AccessLimitInterceptor");
        registry.addInterceptor(paginationInterceptor);
        registry.addInterceptor(accessLimitInterceptor);
    }
}
