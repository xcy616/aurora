package com.aurora.config;

import com.aurora.filter.JwtAuthenticationTokenFilter;
import com.aurora.handler.AccessDecisionManagerImpl;
import com.aurora.handler.FilterInvocationSecurityMetadataSourceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 安全配置（过滤器链组装台）
 *
 * 作用：应用启动时执行一次，组装安全过滤器链
 * 不参与单个请求处理，只负责"装配"
 *
 * 过滤器链核心流程：
 *   JwtAuthenticationTokenFilter（自写，解析JWT存入SecurityContext）
 *   → UsernamePasswordAuthenticationFilter（框架自带，只拦 /users/login）
 *   → ExceptionTranslationFilter（框架自带，接住异常分发给兜底Handler）
 *   → FilterSecurityInterceptor（框架自带，调自定义组件做权限判断）
 *
 * 本配置做的 5 件事：
 * 1. formLogin：激活 UsernamePasswordAuthenticationFilter，配置登录路径和成功/失败Handler
 * 2. ObjectPostProcessor：把 FilterSecurityInterceptor 的两个组件换成自定义的
 * 3. exceptionHandling：配置未登录/权限不足的兜底 Handler
 * 4. STATELESS：无状态会话（纯 JWT，不用 HttpSession）
 * 5. addFilterBefore：把自定义 JWT Filter 插到登录过滤器前面
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private AuthenticationEntryPoint authenticationEntryPoint;        // 未登录兜底

    @Autowired
    private AccessDeniedHandler accessDeniedHandler;                  // 权限不足兜底

    @Autowired
    private AuthenticationSuccessHandler authenticationSuccessHandler; // 登录成功处理

    @Autowired
    private AuthenticationFailureHandler authenticationFailureHandler; // 登录失败处理

    @Autowired
    private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter; // 自定义JWT过滤器

    /**
     * 自定义安全元数据源（查"这个URL需要什么角色"）
     * 从数据库加载资源-角色映射
     */
    @Bean
    public FilterInvocationSecurityMetadataSource securityMetadataSource() {
        return new FilterInvocationSecurityMetadataSourceImpl();
    }

    /**
     * 自定义访问决策管理器（比对"用户角色是否满足"）
     */
    @Bean
    public AccessDecisionManager accessDecisionManager() {
        return new AccessDecisionManagerImpl();
    }

    /**
     * 认证管理器（登录时验证账号密码用）
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * 密码加密器（BCrypt，登录比对密码用）
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // ① 表单登录配置：激活 UsernamePasswordAuthenticationFilter
        //    拦截 POST /users/login，成功/失败分别走对应 Handler
        http.formLogin()
                .loginProcessingUrl("/users/login")
                .successHandler(authenticationSuccessHandler)
                .failureHandler(authenticationFailureHandler);

        // ② 授权配置：把 FilterSecurityInterceptor 内部的两个组件替换成自定义的
        //    securityMetadataSource：根据URL+方法查所需角色
        //    accessDecisionManager：比对用户角色
        http.authorizeRequests()
                .withObjectPostProcessor(new ObjectPostProcessor<FilterSecurityInterceptor>() {
                    @Override
                    public <O extends FilterSecurityInterceptor> O postProcess(O fsi) {
                        fsi.setSecurityMetadataSource(securityMetadataSource());
                        fsi.setAccessDecisionManager(accessDecisionManager());
                        return fsi;
                    }
                })
                .anyRequest().permitAll()  // 默认放行，具体权限交给自定义组件判断
                .and()
                // ③ 异常处理：配置 ExceptionTranslationFilter 的兜底处理器
                .csrf().disable().exceptionHandling()   // JWT 无状态，关闭 CSRF
                .authenticationEntryPoint(authenticationEntryPoint)   // 未登录 → "用户未登录"
                .accessDeniedHandler(accessDeniedHandler)              // 权限不足 → "权限不足"
                .and()
                // ④ 无状态会话：不创建 HttpSession，纯 JWT 认证
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        // ⑤ 把自定义 JWT 过滤器插到 UsernamePasswordAuthenticationFilter 前面
        http.addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
    }

}
