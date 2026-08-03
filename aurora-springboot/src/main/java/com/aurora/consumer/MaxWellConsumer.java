package com.aurora.consumer;

import com.alibaba.fastjson.JSON;
import com.aurora.model.dto.ArticleSearchDTO;
import com.aurora.model.dto.MaxwellDataDTO;
import com.aurora.entity.Article;
import com.aurora.mapper.ElasticsearchMapper;
import com.aurora.util.BeanCopyUtil;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.aurora.constant.RabbitMQConstant.MAXWELL_QUEUE;

/**
 * Maxwell 数据同步消费者
 *
 * 作用：监听 maxwell_queue 队列，把文章变更同步到 Elasticsearch 搜索引擎
 *
 * 背景：Maxwell 工具监听 MySQL binlog，当 t_article 表发生增删改时，
 * 把变更数据发到 MQ，本消费者收到后同步更新 ES 中的文章索引
 *
 * 处理逻辑（按操作类型分发）：
 * - insert/update：把文章保存到 ES（新文章可被搜索，修改后搜索到新内容）
 * - delete：从 ES 删除文章索引
 *
 * 好处：搜索功能和业务解耦，即使 ES 短暂不可用也不影响正常业务
 */
@Component
@RabbitListener(queues = MAXWELL_QUEUE)
public class MaxWellConsumer {

    @Autowired
    private ElasticsearchMapper elasticsearchMapper;

    /**
     * 处理队列消息
     * @param data MaxwellDataDTO 的 JSON 字节数组（含操作类型和数据）
     */
    @RabbitHandler
    public void process(byte[] data) {
        // 反序列化：JSON字节 → MaxwellDataDTO
        MaxwellDataDTO maxwellDataDTO = JSON.parseObject(new String(data), MaxwellDataDTO.class);
        // 取出变更的文章数据
        Article article = JSON.parseObject(JSON.toJSONString(maxwellDataDTO.getData()), Article.class);
        // 按操作类型分发处理
        switch (maxwellDataDTO.getType()) {
            case "insert":
            case "update":
                // 新增/修改 → 保存/更新 ES 索引
                elasticsearchMapper.save(BeanCopyUtil.copyObject(article, ArticleSearchDTO.class));
                break;
            case "delete":
                // 删除 → 移除 ES 索引
                elasticsearchMapper.deleteById(article.getId());
                break;
            default:
                break;
        }
    }
}