package com.example.quotation.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 工具类（增强版）
 * 支持多字段写入、解析与安全密钥管理
 * <p>
 * 功能：
 * 1. 生成包含多个字段的 JWT Token（userId, username, role, email）
 * 2. 解析 Token，返回 Claims（包含自定义字段）
 * 3. 验证 Token 是否过期或无效
 * <p>
 * 注意：
 * - SECRET 应该放在环境变量中（如 Linux export JWT_SECRET="xxxx"）
 * - 切勿将密钥写死在代码里（尤其是生产环境）
 */
public class JwtUtil {

    /**
     * 读取系统环境变量中的密钥，如果未设置则使用默认值（仅限开发）
     */
    private static final String SECRET = System.getenv("JWT_SECRET") != null ?
            System.getenv("JWT_SECRET") : "ReplaceThisSecretForDevelopmentOnly";

    /**
     * Token 有效期（24 小时）
     */
    private static final long EXPIRATION = 1000L * 60 * 60 * 24;

    /**
     * 生成用于签名的密钥对象
     */
    private static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    /**
     * ✅ 生成 JWT Token
     *
     * @param userId   用户ID
     * @param username 用户名
     * @param role     用户角色
     * @param email    用户邮箱
     * @return JWT Token 字符串
     */
    public static String generateToken(Long userId, String username, String role, String email) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + EXPIRATION);

        return Jwts.builder()
                .setSubject(username) // 标准字段：用户名
                .claim("userId", userId) // 自定义字段
                .claim("role", role)
                .claim("email", email)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * ✅ 解析 Token
     *
     * @param token JWT Token
     * @return Claims 对象（包含用户信息）
     * @throws JwtException 如果 Token 无效或过期
     */
    public static Claims parseToken(String token) throws JwtException {
        return Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * ✅ 校验 Token 是否有效
     *
     * @param token JWT Token
     * @return true：有效；false：无效或过期
     */
    public static boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch(JwtException e) {
            return false;
        }
    }

    /**
     * ✅ 从 Token 获取指定字段
     *
     * @param token JWT Token
     * @param key   字段名
     * @return 对应值（可能为 null）
     */
    public static Object getClaim(String token, String key) {
        Claims claims = parseToken(token);
        return claims.get(key);
    }

    /**
     * ✅ 从 Token 获取用户名（subject）
     *
     * @param token JWT Token
     * @return 用户名
     */
    public static String getUsername(String token) {
        return parseToken(token).getSubject();
    }
}
