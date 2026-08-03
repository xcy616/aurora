package com.aurora.constant;

/**
 * 认证相关常量
 *
 * 消费方：TokenServiceImpl（token创建/解析/续期）、JwtAuthenticationTokenFilter
 */
public interface AuthConstant {

    /** 续期判断阈值：距过期不足20分钟时刷新token */
    int TWENTY_MINUTES = 20;

    /** token过期时间：7天（秒） */
    int EXPIRE_TIME = 7 * 24 * 60 * 60;

    /** 请求头名称：Authorization */
    String TOKEN_HEADER = "Authorization";

    /** token前缀：Bearer 后面跟一个空格 */
    String TOKEN_PREFIX = "Bearer ";

}
