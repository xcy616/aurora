package com.aurora.constant;

/**
 * RabbitMQ 队列/交换机名称常量
 *
 * 消费方：RabbitMQConfig（声明队列交换机）、
 *        consumer 包（消费者监听）、发送方（producer）
 */
public interface RabbitMQConstant {

    /** 文章同步队列（文章变更→同步到Elasticsearch） */
    String MAXWELL_QUEUE = "maxwell_queue";

    /** 文章同步交换机 */
    String MAXWELL_EXCHANGE = "maxwell_exchange";

    /** 邮件队列（发邮件通知） */
    String EMAIL_QUEUE = "email_queue";

    /** 邮件交换机 */
    String EMAIL_EXCHANGE = "email_exchange";

    /** 订阅通知队列（文章更新通知订阅者） */
    String SUBSCRIBE_QUEUE = "subscribe_queue";

    /** 订阅通知交换机 */
    String SUBSCRIBE_EXCHANGE = "subscribe_exchange";

}
