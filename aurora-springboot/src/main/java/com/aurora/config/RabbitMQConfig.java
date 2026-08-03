package com.aurora.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.aurora.constant.RabbitMQConstant.*;

/**
 * RabbitMQ 消息队列配置
 *
 * 作用：启动时声明 3 组「队列 + 交换机 + 绑定关系」
 *
 * 为什么用消息队列：邮件发送、文章同步 ES 等操作耗时长，
 * 直接同步做会拖慢接口响应。改成：接口只发消息到队列，立刻返回，
 * 消费者（consumer 包）在后台慢慢处理。
 *
 * 三组队列：
 * 1. maxwell 队列：文章变更 → 同步到 Elasticsearch 搜索引擎
 * 2. email 队列：  发送邮件通知（评论回复、订阅）
 * 3. subscribe 队列：订阅者通知
 */
@Configuration
public class RabbitMQConfig {

    /**
     * ① 文章同步队列（Maxwell 监听 MySQL binlog 发消息）
     */
    @Bean
    public Queue articleQueue() {
        return new Queue(MAXWELL_QUEUE, true);   // durable=true：持久化，MQ重启不丢消息
    }

    /**
     * ① 文章同步交换机（Fanout：广播给所有绑定它的队列）
     */
    @Bean
    public FanoutExchange maxWellExchange() {
        return new FanoutExchange(MAXWELL_EXCHANGE, true, false);
    }

    /**
     * ① 绑定：交换机 → 队列
     */
    @Bean
    public Binding bindingArticleDirect() {
        return BindingBuilder.bind(articleQueue()).to(maxWellExchange());
    }

    /**
     * ② 邮件队列（发邮件通知）
     */
    @Bean
    public Queue emailQueue() {
        return new Queue(EMAIL_QUEUE, true);
    }

    /**
     * ② 邮件交换机
     */
    @Bean
    public FanoutExchange emailExchange() {
        return new FanoutExchange(EMAIL_EXCHANGE, true, false);
    }

    /**
     * ② 绑定：交换机 → 队列
     */
    @Bean
    public Binding bindingEmailDirect() {
        return BindingBuilder.bind(emailQueue()).to(emailExchange());
    }

    /**
     * ③ 订阅通知队列（文章更新通知订阅者）
     */
    @Bean
    public Queue subscribeQueue() {
        return new Queue(SUBSCRIBE_QUEUE, true);
    }

    /**
     * ③ 订阅通知交换机
     */
    @Bean
    public FanoutExchange subscribeExchange() {
        return new FanoutExchange(SUBSCRIBE_EXCHANGE, true, false);
    }

    /**
     * ③ 绑定：交换机 → 队列
     */
    @Bean
    public Binding bindingSubscribeDirect() {
        return BindingBuilder.bind(subscribeQueue()).to(subscribeExchange());
    }

}
