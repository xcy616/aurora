package com.aurora.config;

import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

import static com.aurora.enums.ZoneEnum.SHANGHAI;

/**
 * 全局时区配置
 *
 * 作用：把 JVM 默认时区设置为上海时区（东八区）
 * 为什么需要：服务器可能在国外（时区不是东八区），
 * 会导致 LocalDateTime.now() 等时间 API 返回错误的时间
 * 设置后：所有时间相关的代码（创建时间、更新时间）都按北京时间生成
 */
@Configuration
public class GlobalZoneConfig {

    /**
     * 应用启动时执行，设置默认时区为上海
     */
    @PostConstruct
    public void setGlobalZone() {
        TimeZone.setDefault(TimeZone.getTimeZone(SHANGHAI.getZone()));
    }

}
