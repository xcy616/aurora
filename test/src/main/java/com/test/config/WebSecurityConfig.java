package com.test.config;

import com.test.filter.JwtAuthenticationTokenFilter;
import com.test.handler.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 配置（过滤器链组装台）
 *
 * 原项目路径：com.aurora.config.WebSecurityConfig
 *
 * 这个类不参与单个请求处理，只在应用启动时执行一次，作用是"组装"过滤器链：
 *
 *   ① 配置表单登录 → 激活 UsernamePasswordAuthenticationFilter
 *   ② 配置动态权限 → 替换 FilterSecurityInterceptor 内部的两个组件
 *   ③ 配置异常处理 → 设置 ExceptionTranslationFilter 的兜底处理器
 *   ④ 配置无状态会话 → 关闭 Session（纯 JWT）
 *   ⑤ 插入 JWT Filter → 在 UsernamePasswordAuthenticationFilter 前面
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private com.test.handler.AuthenticationEntryPointImpl authenticationEntryPoint;

    @Autowired
    private com.test.handler.AccessDeniedHandlerImpl accessDeniedHandler;

    @Autowired
    private com.test.handler.AuthenticationSuccessHandlerImpl authenticationSuccessHandler;

    @Autowired
    private com.test.handler.AuthenticationFailHandlerImpl authenticationFailureHandler;

    @Autowired
    private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;

    /**
     * 密码加密器
     * 登录时框架用这个比对密码
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 认证管理器
     * 登录时 UsernamePasswordAuthenticationFilter 调它来验证账号密码
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        System.out.println("[WebSecurityConfig] 开始组装过滤器链...");

        // ① 配置表单登录 → 激活 UsernamePasswordAuthenticationFilter
        //    拦截 POST /users/login，成功调 successHandler，失败调 failureHandler
        http.formLogin()
                .loginProcessingUrl("/users/login")
                .successHandler(authenticationSuccessHandler)
                .failureHandler(authenticationFailureHandler);

        // ② 配置动态权限 → 替换 FilterSecurityInterceptor 内部的两个组件
        //    securityMetadataSource：查"这个URL需要什么角色"
        //    accessDecisionManager：比对"用户角色是否满足"
        http.authorizeRequests()
                .withObjectPostProcessor(new ObjectPostProcessor<FilterSecurityInterceptor>() {
                    @Override
                    public <O extends FilterSecurityInterceptor> O postProcess(O fsi) {
                        fsi.setSecurityMetadataSource(
                                new com.test.handler.FilterInvocationSecurityMetadataSourceImpl());
                        fsi.setAccessDecisionManager(
                                new com.test.handler.AccessDecisionManagerImpl());
                        return fsi;
                    }
                })
                .anyRequest().permitAll();  // 默认放行，具体权限交给自定义组件判断

        // ③ 配置异常处理 → 设置 ExceptionTranslationFilter 的兜底处理器
        //    CSRF 关掉（JWT 不需要）
        http.csrf().disable()
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint)  // 未登录 → 返回 "用户未登录"
                .accessDeniedHandler(accessDeniedHandler);            // 权限不足 → 返回 "权限不足"

        // ④ 配置无状态会话 → 关闭 Session
        //    纯 JWT 无状态认证，不创建 HttpSession
        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // ⑤ 插入 JWT Filter
        //    把自定义的 JwtAuthenticationTokenFilter 插到 UsernamePasswordAuthenticationFilter 前面
        //    这样每个请求先经过 JWT 认证，再做登录/权限判断
        http.addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);

        System.out.println("[WebSecurityConfig] 过滤器链组装完成！");
    }
}
