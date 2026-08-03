package com.aurora.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * MinIO 配置属性类
 *
 * 作用：把 application.yml 里 upload.minio.* 配置项绑定到这个类
 * 消费方：MinioUploadStrategyImpl（上传策略）
 *
 * 对应配置：
 *   upload:
 *     minio:
 *       url: http://localhost:9000
 *       endpoint: http://localhost:9000
 *       access-key: minioadmin
 *       secret-key: minioadmin
 *       bucket-name: aurora
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "upload.minio")
public class MinioProperties {

    /** 访问地址（拼接文件完整URL用） */
    private String url;

    /** MinIO 服务地址（SDK连接用） */
    private String endpoint;

    /** 访问密钥 AccessKey */
    private String accessKey;

    /** 访问密钥 SecretKey */
    private String secretKey;

    /** 存储桶名称（文件都放这个桶里） */
    private String bucketName;
}
