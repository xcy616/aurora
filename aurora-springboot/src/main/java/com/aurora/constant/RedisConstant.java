package com.aurora.constant;

/**
 * Redis key 常量
 *
 * 作用：统一管理所有 Redis 的 key，防止字符串拼写不一致
 * 消费方：RedisServiceImpl、各 ServiceImpl
 */
public interface RedisConstant {

    /** 验证码过期时间：15分钟（秒） */
    long CODE_EXPIRE_TIME = 15 * 60;

    /** 邮箱验证码 key 前缀：code:xxx */
    String USER_CODE_KEY = "code:";

    /** 博客访问量 */
    String BLOG_VIEWS_COUNT = "blog_views_count";

    /** 文章浏览量（hash：articleId → 浏览量） */
    String ARTICLE_VIEWS_COUNT = "article_views_count";

    /** 网站配置（JSON字符串） */
    String WEBSITE_CONFIG = "website_config";

    /** 用户分布区域（hash） */
    String USER_AREA = "user_area";

    /** 访客分布区域（hash） */
    String VISITOR_AREA = "visitor_area";

    /** 关于我内容 */
    String ABOUT = "about";

    /** 独立访客数（IP去重） */
    String UNIQUE_VISITOR = "unique_visitor";

    /** 登录用户信息（hash：userId → UserDetailsDTO） */
    String LOGIN_USER = "login_user";

    /** 文章访问密码（hash：articleId → 密码） */
    String ARTICLE_ACCESS = "article_access:";

}
