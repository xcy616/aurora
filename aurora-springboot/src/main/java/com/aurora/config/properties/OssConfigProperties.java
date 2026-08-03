package com.aurora.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 阿里云 OSS 配置属性类
 *
 * 作用：把 application.yml 里 upload.oss.* 配置项绑定到这个类
 * 消费方：OssUploadStrategyImpl（上传策略）
 *
 * 与 MinioProperties 的区别：这是阿里云对象存储的配置，
 * 项目通过 UploadModeEnum 枚举选择用 MinIO 还是 OSS
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "upload.oss")
public class OssConfigProperties {

    /** 访问地址（拼接文件完整URL用） */
    private String url;

    /** OSS 服务地址 */
    private String endpoint;

    /** AccessKey ID */
    private String accessKeyId;

    /** AccessKey Secret */
    private String accessKeySecret;

    /** 存储桶名称 */
    private String bucketName;

}
