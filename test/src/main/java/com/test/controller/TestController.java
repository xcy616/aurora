package com.test.controller;

import com.test.annotation.AccessLimit;
import com.test.annotation.OptLog;
import com.test.model.ResultVO;
import com.test.util.PageUtil;
import com.test.util.UserUtil;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 测试接口
 *
 * 演示拦截器链路：
 *   ① 能否通过 JWT Filter（认证）
 *   ② 能否通过 FilterSecurityInterceptor（授权）
 *   ③ 能否通过 PaginationInterceptor（分页参数提取）
 *   ④ 能否通过 AccessLimitInterceptor（限流检查）
 *   ⑤ 能否触发 OperationLogAspect（操作日志）
 *   ⑥ 能否触发 ExceptionLogAspect（异常日志）
 */
@RestController
public class TestController {

    // ==================== 公开接口（不需要权限） ====================

    /**
     * 公开接口 - 任何人都能访问
     */
    @GetMapping("/public/hello")
    public ResultVO<String> publicHello() {
        System.out.println("[Controller] 执行 publicHello()");
        return ResultVO.ok("你好，这是公开接口！不需要登录就能访问。");
    }

    /**
     * 公开接口 - 带分页参数
     *
     * 请求示例：GET /public/articles?current=1&size=5
     * → PaginationInterceptor 提取分页参数
     */
    @GetMapping("/public/articles")
    public ResultVO<Map<String, Object>> publicArticles() {
        PageUtil.Page page = PageUtil.getCurrentPage();
        System.out.println("[Controller] 执行 publicArticles()，分页参数: " + page);

        Map<String, Object> data = new HashMap<>();
        data.put("page", page != null ? page : "未传递分页参数");
        data.put("articles", new String[]{"文章1", "文章2", "文章3"});
        return ResultVO.ok(data);
    }

    // ==================== 需要 admin 角色的接口 ====================

    /**
     * Admin 接口 - 需要 admin 角色
     *
     * 安全流程：
     *   ① JwtFilter → 提取 token → 存入 SecurityContext
     *   ② FilterSecurityInterceptor →
     *       SecurityMetadataSource: /admin/** + GET → 需要 ["admin"]
     *       AccessDecisionManager: 用户角色 ["admin"] 包含 "admin" → 放行
     *
     * 测试：admin 用户能访问，user 用户会返回"权限不足"
     */
    @GetMapping("/admin/hello")
    public ResultVO<String> adminHello() {
        String user = UserUtil.getUserDetailsDTO().getUsername();
        System.out.println("[Controller] 执行 adminHello()，用户名: " + user);
        return ResultVO.ok("你好，" + user + "！你有 admin 权限，可以访问管理接口。");
    }

    /**
     * Admin 接口 - 带 @OptLog 操作日志
     *
     * 测试：访问成功后，控制台会看到 [OperationLog] 输出
     */
    @OptLog(optType = "新增")
    @PostMapping("/admin/create")
    public ResultVO<String> adminCreate(@RequestBody Map<String, String> body) {
        System.out.println("[Controller] 执行 adminCreate()，参数: " + body);
        return ResultVO.ok("创建成功");
    }

    /**
     * Admin 接口 - 带 @AccessLimit 限流
     *
     * @AccessLimit(seconds = 10, maxCount = 3)
     * 表示：10秒内最多请求3次
     * 超过3次 → AccessLimitInterceptor 拦截，返回"请求过于频繁"
     *
     * 测试：快速连续访问4次，第4次会被拦截
     */
    @AccessLimit(seconds = 10, maxCount = 3)
    @GetMapping("/admin/limited")
    public ResultVO<String> adminLimited() {
        System.out.println("[Controller] 执行 adminLimited()");
        return ResultVO.ok("限流接口访问成功！10秒内你别再点太多次哦。");
    }

    // ==================== 需要 user 角色的接口 ====================

    /**
     * 用户接口 - 需要 user 角色
     *
     * admin 用户也有 user 角色，所以 admin 也可以访问
     * 没登录的用户不能访问（返回"用户未登录"）
     */
    @GetMapping("/user/profile")
    public ResultVO<Map<String, String>> userProfile() {
        String user = UserUtil.getUserDetailsDTO().getUsername();
        System.out.println("[Controller] 执行 userProfile()，用户名: " + user);

        Map<String, String> data = new HashMap<>();
        data.put("username", user);
        data.put("message", "这是你的个人资料页");
        return ResultVO.ok(data);
    }

    // ==================== 异常测试接口 ====================

    /**
     * 故意抛异常的接口
     * 测试：ExceptionLogAspect 会捕获并记录异常信息
     */
    @GetMapping("/admin/error")
    public ResultVO<String> adminError() {
        System.out.println("[Controller] 执行 adminError()，即将抛异常...");
        throw new RuntimeException("这是一个测试异常");
    }
}
