package com.aurora.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 文件路径枚举（MinIO 桶内的对象目录）
 *
 * 注意：路径不含桶名前缀。MinIO 访问 URL 由 "桶名 + 对象路径" 组成，
 * 桶名已在 MinioProperties.bucketName 配置（如 aurora），
 * 若这里再加 "aurora/" 前缀会导致 URL 拼出 /aurora/aurora/xxx 访问不到。
 */
@Getter
@AllArgsConstructor
public enum FilePathEnum {

    AVATAR("avatar/", "头像路径"),

    ARTICLE("articles/", "文章图片路径"),

    VOICE("voice/", "音频路径"),

    PHOTO("photos/", "相册路径"),

    CONFIG("config/", "配置图片路径"),

    TALK("talks/", "配置图片路径"),

    MD("markdown/", "md文件路径");

    private final String path;

    private final String desc;

}
