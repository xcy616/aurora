package com.test.interceptor;

import com.alibaba.fastjson.JSON;
import com.test.annotation.AccessLimit;
import com.test.model.ResultVO;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 接口限流拦截器
 *
 * 原项目路径：com.aurora.interceptor.AccessLimitInterceptor
 *
 * 作用：检查方法上有没有 @AccessLimit 注解，有就做限流
 *       原项目用 Redis INCR 原子操作 + 过期时间，这里用内存 ConcurrentHashMap 模拟
 *
 * 工作流程：
 *   1. 检查方法上有没有 @AccessLimit 注解
 *   2. 没有 → 放行
 *   3. 有 → 根据 IP + 方法名 生成 key，计数器 +1
 *   4. 超过 maxCount → 返回 "请求过于频繁"
 *   5. 未超过 → 放行
 *
 * 注解用法：
 *   @AccessLimit(seconds = 60, maxCount = 5)
 *   → 60秒内最多允许请求5次
 */
@Component
public class AccessLimitInterceptor implements HandlerInterceptor {

    /**
     * 内存计数器（原项目用 Redis）
     * key = "IP-方法名-窗口起始时间"
     * value = 当前窗口内的请求次数
     */
    private final ConcurrentHashMap<String, Integer> counter = new ConcurrentHashMap<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        AccessLimit accessLimit = handlerMethod.getMethodAnnotation(AccessLimit.class);

        // 没有 @AccessLimit 注解，不限流，直接放行
        if (accessLimit == null) {
            return true;
        }

        long seconds = accessLimit.seconds();
        int maxCount = accessLimit.maxCount();

        // key = IP + 方法名 + 时间窗口
        // 原项目 key = IP-方法名，用 Redis EXPIRE 自动清理
        // 这里加上时间窗口区分不同时间段
        long windowIndex = System.currentTimeMillis() / (seconds * 1000);
        String key = request.getRemoteAddr() + "-" + handlerMethod.getMethod().getName() + "-" + windowIndex;

        int current = counter.getOrDefault(key, 0) + 1;
        counter.put(key, current);

        System.out.println("[AccessLimitInterceptor] " + key + " → " + current + "/" + maxCount);

        if (current > maxCount) {
            System.out.println("[AccessLimitInterceptor] 限流触发！" + seconds + "秒内超过" + maxCount + "次");
            render(response, ResultVO.fail("请求过于频繁，" + seconds + "秒后再试"));
            return false; // 拦截！不放行
        }
        return true;
    }

    /**
     * 直接向响应写入 JSON 错误信息
     * 因为请求被拦截了，不会进入 Controller
     */
    private void render(HttpServletResponse response, ResultVO<?> resultVO) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        OutputStream out = response.getOutputStream();
        String str = JSON.toJSONString(resultVO);
        out.write(str.getBytes(StandardCharsets.UTF_8));
        out.flush();
        out.close();
    }
}
