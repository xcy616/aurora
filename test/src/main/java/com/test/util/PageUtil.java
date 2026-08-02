package com.test.util;

/**
 * 分页工具类（基于 ThreadLocal）
 *
 * 原项目路径：com.aurora.util.PageUtil
 * 作用：通过 ThreadLocal 在同一个请求内传递分页参数
 *
 * 流程：
 *   1. PaginationInterceptor.preHandle() 从请求参数提取分页信息，存入 ThreadLocal
 *   2. Service 层调用 PageUtil.getCurrentPage() 取出分页参数
 *   3. PaginationInterceptor.afterCompletion() 清理 ThreadLocal
 */
public class PageUtil {

    /**
     * 简化版的分页对象（原项目用的是 MyBatis-Plus 的 Page）
     */
    public static class Page {
        public long current;
        public long size;

        public Page(long current, long size) {
            this.current = current;
            this.size = size;
        }

        @Override
        public String toString() {
            return "Page{current=" + current + ", size=" + size + "}";
        }
    }

    private static final ThreadLocal<Page> PAGE_HOLDER = new ThreadLocal<>();

    public static void setCurrentPage(Page page) {
        PAGE_HOLDER.set(page);
    }

    public static Page getCurrentPage() {
        return PAGE_HOLDER.get();
    }

    public static void remove() {
        PAGE_HOLDER.remove();
    }
}
