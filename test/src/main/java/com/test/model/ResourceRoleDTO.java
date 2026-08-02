package com.test.model;

import lombok.Data;

import java.util.List;

/**
 * 资源-角色映射（一条记录 = 一个接口需要什么角色）
 *
 * 原项目路径：com.aurora.model.dto.ResourceRoleDTO
 * 作用：描述某个 URL + 请求方法 需要哪些角色才能访问
 *
 * 示例：
 *   url = "/admin/**"
 *   requestMethod = "GET"
 *   roleList = ["admin"]
 *   含义：GET 请求 /admin/** 路径需要 admin 角色
 */
@Data
public class ResourceRoleDTO {

    /**
     * 资源URL，支持 AntPathMatcher 通配符
     * 比如 "/admin/**" 匹配 /admin/users、/admin/roles 等
     */
    private String url;

    /**
     * HTTP 方法：GET、POST、PUT、DELETE
     */
    private String requestMethod;

    /**
     * 这个资源需要哪些角色
     * 空列表 = 谁都不能访问（disable）
     * null = 不需要权限
     */
    private List<String> roleList;
}
