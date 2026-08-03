package com.aurora.consumer;

import com.alibaba.fastjson.JSON;
import com.aurora.model.dto.EmailDTO;
import com.aurora.util.EmailUtil;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import static com.aurora.constant.RabbitMQConstant.EMAIL_QUEUE;

/**
 * 评论通知邮件消费者
 *
 * 作用：监听 email_queue 队列，异步发送邮件
 * 触发场景：评论回复、@提醒等需要发邮件的操作
 *
 * 流程：
 * 1. 业务方把 EmailDTO 转成 JSON 字节数组，发到 email_queue 队列
 * 2. 本消费者收到消息，反序列化回 EmailDTO
 * 3. 调用 EmailUtil.sendHtmlMail() 发送 HTML 邮件
 *
 * 好处：发邮件耗时（网络IO），异步化后用户不用等邮件发完才收到响应
 */
@Component
@RabbitListener(queues = EMAIL_QUEUE)
public class CommentNoticeConsumer {

    @Autowired
    private EmailUtil emailUtil;

    /**
     * 处理队列消息
     * @param data 消息内容（EmailDTO 的 JSON 字节数组）
     */
    @RabbitHandler
    public void process(byte[] data) {
        // 反序列化：JSON字节 → EmailDTO
        EmailDTO emailDTO = JSON.parseObject(new String(data), EmailDTO.class);
        // 发送 HTML 邮件
        emailUtil.sendHtmlMail(emailDTO);
    }

}
