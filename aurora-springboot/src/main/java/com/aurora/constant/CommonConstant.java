package com.aurora.constant;

/**
 * 通用常量
 *
 * 全项目共享的固定值，统一收口在这里，防止魔法数字/魔法字符串散落各处
 */
public interface CommonConstant {

    /** 数字1（通用） */
    int ONE = 1;

    /** 数字0（通用） */
    int ZERO = 0;

    /** 逻辑假 */
    int FALSE = 0;

    /** 逻辑真 */
    int TRUE = 1;

    /** 博主用户ID（特殊角色：博主） */
    int BLOGGER_ID = 1;

    /** 网站配置表默认记录ID */
    int DEFAULT_CONFIG_ID = 1;

    /** 关于我表默认记录ID */
    int DEFAULT_ABOUT_ID = 1;

    /** 搜索高亮前缀标签 */
    String PRE_TAG = "<mark>";

    /** 搜索高亮后缀标签 */
    String POST_TAG = "</mark>";

    /** 分页参数：当前页 */
    String CURRENT = "current";

    /** 分页参数：每页条数 */
    String SIZE = "size";

    /** 分页参数：每页默认10条 */
    String DEFAULT_SIZE = "10";

    /** 默认昵称 */
    String DEFAULT_NICKNAME = "用户";

    /** 前端布局组件名（动态路由用） */
    String COMPONENT = "Layout";

    /** 未知（IP归属地查不到时） */
    String UNKNOWN = "未知";

    /** JSON响应内容类型 */
    String APPLICATION_JSON = "application/json;charset=utf-8";

    /** 邮件类型：验证码 */
    String CAPTCHA = "验证码";

    /** 邮件类型：审核提醒 */
    String CHECK_REMIND = "审核提醒";

    /** 邮件类型：评论提醒 */
    String COMMENT_REMIND = "评论提醒";

    /** 邮件类型：@提醒 */
    String MENTION_REMIND = "@提醒";

}
