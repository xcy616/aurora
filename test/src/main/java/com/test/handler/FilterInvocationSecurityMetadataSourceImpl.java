package com.test.handler;

import com.test.model.ResourceRoleDTO;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * 安全元数据源（查资源需要什么角色）
 *
 * 原项目路径：com.aurora.handler.FilterInvocationSecurityMetadataSourceImpl
 * 作用：根据请求的 URL + HTTP方法，查出访问这个资源需要什么角色
 *
 * 原项目：应用启动时从数据库 t_resource + t_role 查出所有「资源-角色」映射
 * 这里：直接硬编码，模拟从数据库查出来的数据
 *
 * 工作流程：
 *   1. 启动时 loadResourceRoleList() 加载所有资源-角色映射到内存
 *   2. 每次请求 getAttributes() 被调用，遍历内存列表匹配 URL+Method
 *   3. 匹配到了 → 返回这个资源需要的角色列表
 *   4. 没匹配到 → 返回 null（不需要权限，放行）
 */
@Component
public class FilterInvocationSecurityMetadataSourceImpl implements FilterInvocationSecurityMetadataSource {

    /**
     * 内存中的资源-角色映射列表
     * 原项目：roleMapper.listResourceRoles() 从数据库查
     */
    private List<ResourceRoleDTO> resourceRoleList;

    /**
     * 初始化（原项目用 @PostConstruct 从数据库加载）
     */
    public FilterInvocationSecurityMetadataSourceImpl() {
        loadResourceRoleList();
    }

    private void loadResourceRoleList() {
        ResourceRoleDTO adminResource = new ResourceRoleDTO();
        adminResource.setUrl("/admin/**");
        adminResource.setRequestMethod("GET");
        adminResource.setRoleList(Arrays.asList("admin"));

        ResourceRoleDTO userResource = new ResourceRoleDTO();
        userResource.setUrl("/user/**");
        userResource.setRequestMethod("GET");
        userResource.setRoleList(Arrays.asList("user"));

        ResourceRoleDTO adminPost = new ResourceRoleDTO();
        adminPost.setUrl("/admin/**");
        adminPost.setRequestMethod("POST");
        adminPost.setRoleList(Arrays.asList("admin"));

        ResourceRoleDTO adminDelete = new ResourceRoleDTO();
        adminDelete.setUrl("/admin/**");
        adminDelete.setRequestMethod("DELETE");
        adminDelete.setRoleList(Arrays.asList("admin"));

        resourceRoleList = Arrays.asList(adminResource, adminPost, adminDelete, userResource);
        System.out.println("[SecurityMetadataSource] 加载资源-角色映射完成，共 " + resourceRoleList.size() + " 条");
    }

    /**
     * 核心方法：每个请求都调用
     * 参数 object 就是当前请求的 FilterInvocation
     * 返回值 = 这个资源需要的角色列表（ConfigAttribute 集合）
     */
    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) {
        FilterInvocation fi = (FilterInvocation) object;
        String method = fi.getRequest().getMethod();     // 比如 "GET"
        String url = fi.getRequest().getRequestURI();    // 比如 "/admin/users"

        AntPathMatcher matcher = new AntPathMatcher();

        // 遍历所有资源规则，看哪个匹配上了
        for (ResourceRoleDTO resource : resourceRoleList) {
            if (matcher.match(resource.getUrl(), url) && resource.getRequestMethod().equals(method)) {
                // 匹配到了！返回这个资源需要的角色列表
                List<String> roleList = resource.getRoleList();
                System.out.println("[SecurityMetadataSource] 匹配到资源 " + resource.getUrl()
                        + "，需要角色: " + roleList);
                return SecurityConfig.createList(roleList.toArray(new String[]{}));
            }
        }

        // 没匹配到任何资源规则，说明这个路径不需要权限
        System.out.println("[SecurityMetadataSource] " + url + " 不需要权限，返回 null");
        return null;
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return FilterInvocation.class.isAssignableFrom(clazz);
    }
}
