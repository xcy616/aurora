package com.test.service.impl;

import com.test.model.UserDetailsDTO;
import com.test.service.TokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Token 服务实现（内存版）
 *
 * 原项目路径：com.aurora.service.impl.TokenServiceImpl
 * 区别：原项目用 Redis 存用户信息，这里用 ConcurrentHashMap 模拟，效果一样
 *
 * 核心流程：
 *   1. 登录成功 → createToken() → 把用户信息存入 Map（key=userId），生成 JWT（subject=userId）
 *   2. 每次请求 → getUserDetailDTO() → 从 Header 取 JWT → 解析出 userId → 从 Map 取用户信息
 *   3. 快过期时 → renewToken() → 刷新 Map 中的过期时间
 */
@Service
public class TokenServiceImpl implements TokenService {

    @Value("${jwt.secret}")
    private String secret;

    /**
     * 模拟 Redis 的 hash 结构
     * key = "login_user"
     * value = Map<userId, UserDetailsDTO>
     *
     * 原项目：redisService.hSet("login_user", userId, userDetailsDTO)
     * 这里：  USER_STORE.put(userId, userDetailsDTO)
     */
    private final ConcurrentHashMap<String, UserDetailsDTO> USER_STORE = new ConcurrentHashMap<>();

    @Override
    public String createToken(UserDetailsDTO userDetailsDTO) {
        String userId = userDetailsDTO.getId().toString();
        // 存入"Redis"（内存Map）
        USER_STORE.put(userId, userDetailsDTO);
        // 生成 JWT（subject 就是 userId，JWT 只存了 userId）
        return createJwtToken(userId);
    }

    private String createJwtToken(String subject) {
        SecretKey secretKey = generalKey();
        return Jwts.builder()
                .setId(UUID.randomUUID().toString().replace("-", ""))
                .setSubject(subject)  // 把 userId 塞进 JWT
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    @Override
    public UserDetailsDTO getUserDetailDTO(HttpServletRequest request) {
        // 从 Header 取 "Authorization: Bearer xxx"
        String token = request.getHeader("Authorization");
        if (StringUtils.isEmpty(token)) {
            token = "";
        } else {
            token = token.replaceFirst("Bearer ", "");
        }

        if (StringUtils.hasText(token) && !token.equals("null")) {
            // 解析 JWT，取出 subject（userId）
            Claims claims = parseToken(token);
            String userId = claims.getSubject();
            // 从"Redis"（内存Map）取出用户完整信息
            return USER_STORE.get(userId);
        }
        return null;
    }

    @Override
    public void renewToken(UserDetailsDTO userDetailsDTO) {
        // 简化版：直接重新存入，原项目会检查是否快过期了才刷新
        USER_STORE.put(userDetailsDTO.getId().toString(), userDetailsDTO);
        // 打印日志方便观察
        System.out.println("[TokenService] 续期用户 token: " + userDetailsDTO.getUsername());
    }

    public Claims parseToken(String token) {
        SecretKey secretKey = generalKey();
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    }

    private SecretKey generalKey() {
        byte[] encodedKey = Base64.getDecoder().decode(secret);
        return new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
    }
}
