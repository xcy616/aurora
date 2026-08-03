package com.aurora.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * QQ 登录配置属性类
 *
 * 作用：把 application.yml 里 qq.* 配置项绑定到这个类
 * 消费方：QqLoginStrategyImpl（第三方登录策略）
 *
 * QQ 登录流程（OAuth2）：
 * 1. 前端跳转 QQ 授权页 → 用户同意 → QQ 回调带 code
 * 2. 后端拿 code 调 check-token-url 换取 access_token
 * 3. 再拿 token 调 user-info-url 获取用户昵称、头像
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "qq")
public class QQConfigProperties {

    /** QQ 互联应用的 AppId */
    private String appId;

    /** 校验授权 code 换取 token 的接口地址 */
    private String checkTokenUrl;

    /** 获取 QQ 用户信息的接口地址 */
    private String userInfoUrl;

}
