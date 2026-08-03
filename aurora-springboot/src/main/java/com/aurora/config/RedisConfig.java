package com.aurora.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis 配置
 *
 * 作用：自定义 RedisTemplate 的序列化方式
 * 为什么需要：默认的 JdkSerializationRedisSerializer 存的是二进制，
 * 存中文/JSON对象后 Redis 客户端里看到的是乱码，跨语言也读不了
 *
 * 本配置实现：
 * - key 用 String 序列化（Redis 里显示明文，如 login_user、article_view_count）
 * - value 用 JSON 序列化（存的是 JSON 字符串，可读性好）
 */
@Configuration
public class RedisConfig {

    /**
     * 自定义 RedisTemplate<String, Object>
     * @param factory Redis 连接工厂（由 Spring Boot 自动配置注入）
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(factory);

        // value 序列化器：Jackson JSON（对象 → JSON 字符串）
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper mapper = new ObjectMapper();
        // 开启类型信息：反序列化时才能还原成原来的类型（比如 UserDetailsDTO）
        mapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        jackson2JsonRedisSerializer.setObjectMapper(mapper);

        // key 序列化器：String（键保持可读）
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

        // 分别设置 key 和 value 的序列化器（hash 结构同理）
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

}
