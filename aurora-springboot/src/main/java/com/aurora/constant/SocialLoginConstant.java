package com.aurora.constant;

/**
 * 第三方登录常量
 *
 * 消费方：strategy/social 包（QQ/Gitee 登录策略）
 * 作用：定义第三方接口返回 JSON 中的字段名
 */
public interface SocialLoginConstant {

    /** QQ 用户唯一标识（openid） */
    String QQ_OPEN_ID = "openid";

    /** OAuth2 授权令牌 */
    String ACCESS_TOKEN = "access_token";

    /** QQ 互联的应用标识 */
    String OAUTH_CONSUMER_KEY = "oauth_consumer_key";

}
