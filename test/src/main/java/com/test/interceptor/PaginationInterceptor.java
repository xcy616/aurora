package com.test.interceptor;

import com.test.util.PageUtil;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;
import java.util.Optional;

/**
 * 分页拦截器
 *
 * 原项目路径：com.aurora.interceptor.PaginationInterceptor
 * 
 * 作用：从请求参数中提取分页信息，存入 ThreadLocal
 *       后续 Service 层通过 PageUtil.getCurrentPage() 直接取用
 *
 * 请求示例：GET /articles?current=1&size=10
 *   → 拦截器提取 current=1, size=10
 *   → 存入 ThreadLocal
 *   → Service 层 PageUtil.getCurrentPage() → Page{current=1, size=10}
 *   → 请求结束 afterCompletion 清理 ThreadLocal
 */
@Component
public class PaginationInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 从请求参数取当前页，默认第1页
        String currentPage = request.getParameter("current");
        // 每页条数，默认10
        String pageSize = Optional.ofNullable(request.getParameter("size")).orElse("10");

        if (!Objects.isNull(currentPage) && !StringUtils.isEmpty(currentPage)) {
            PageUtil.Page page = new PageUtil.Page(Long.parseLong(currentPage), Long.parseLong(pageSize));
            PageUtil.setCurrentPage(page);
            System.out.println("[PaginationInterceptor] 提取分页参数: " + page);
        }
        return true; // 永远放行
    }

    /**
     * 请求结束后清理 ThreadLocal，防止内存泄漏
     * 无论请求成功还是失败都会执行
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        PageUtil.remove();
    }
}
