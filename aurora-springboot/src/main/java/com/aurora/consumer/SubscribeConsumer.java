package com.aurora.consumer;


import com.alibaba.fastjson.JSON;
import com.aurora.entity.Article;
import com.aurora.entity.UserInfo;
import com.aurora.model.dto.EmailDTO;
import com.aurora.service.ArticleService;
import com.aurora.service.UserInfoService;
import com.aurora.util.EmailUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.aurora.constant.CommonConstant.TRUE;
import static com.aurora.constant.RabbitMQConstant.SUBSCRIBE_QUEUE;

/**
 * 订阅通知消费者
 *
 * 作用：监听 subscribe_queue 队列，文章发布/更新时给所有订阅用户发邮件
 *
 * 流程：
 * 1. 发布/更新文章时，业务方把 articleId 发到 subscribe_queue
 * 2. 本消费者查到文章信息 + 所有订阅了邮件的用户
 * 3. 逐个发送订阅通知邮件（含文章链接）
 */
@Component
@RabbitListener(queues = SUBSCRIBE_QUEUE)
public class SubscribeConsumer {

    /** 网站地址（拼接文章链接用） */
    @Value("${website.url}")
    private String websiteUrl;

    @Autowired
    private ArticleService articleService;      // 查文章信息

    @Autowired
    private UserInfoService userInfoService;    // 查订阅用户

    @Autowired
    private EmailUtil emailUtil;                // 发邮件工具

    /**
     * 处理队列消息：给所有订阅者发文章通知邮件
     * @param data 消息内容（articleId 的 JSON 字节数组）
     */
    @RabbitHandler
    public void process(byte[] data) {
        // 反序列化出文章ID
        Integer articleId = JSON.parseObject(new String(data), Integer.class);
        // 查文章详情
        Article article = articleService.getOne(new LambdaQueryWrapper<Article>().eq(Article::getId, articleId));
        // 查所有订阅了通知的用户
        List<UserInfo> users = userInfoService.list(new LambdaQueryWrapper<UserInfo>().eq(UserInfo::getIsSubscribe, TRUE));
        // 提取所有邮箱
        List<String> emails = users.stream().map(UserInfo::getEmail).collect(Collectors.toList());
        // 逐个发邮件
        for (String email : emails) {
            EmailDTO emailDTO = new EmailDTO();
            Map<String, Object> map = new HashMap<>();
            emailDTO.setEmail(email);
            emailDTO.setSubject("文章订阅");
            emailDTO.setTemplate("common.html");   // 邮件模板
            String url = websiteUrl + "/articles/" + articleId;  // 文章链接
            // 判断是新增还是更新（updateTime 为空 = 新增，有值 = 更新过）
            if (article.getUpdateTime() == null) {
                map.put("content", "xcy的个人博客发布了新的文章，"
                        + "<a style=\"text-decoration:none;color:#12addb\" href=\"" + url + "\">点击查看</a>");
            } else {
                map.put("content", "xcy的个人博客对《" + article.getArticleTitle() + "》进行了更新，"
                        + "<a style=\"text-decoration:none;color:#12addb\" href=\"" + url + "\">点击查看</a>");
            }
            emailDTO.setCommentMap(map);   // 邮件模板里的动态变量
            emailUtil.sendHtmlMail(emailDTO);
        }
    }

}
