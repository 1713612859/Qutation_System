package com.example.quotation.config;

import com.example.quotation.util.JwtUtil;
import io.jsonwebtoken.JwtException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JWT过滤器（增强版）
 * 功能：
 * 1. 支持CORS跨域
 * 2. 放行OPTIONS请求
 * 3. 白名单放行
 * 4. 校验并解析JWT Token
 */
@Component
public class JwtFilter extends OncePerRequestFilter {

    // ✅ 不需要JWT验证的白名单接口
    private static final String[] WHITELIST = {
            "/api/auth/login",
            "/api/auth/register"
    };

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // ✅ 设置跨域（可改成从配置读取）
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type");
        response.setHeader("Access-Control-Allow-Credentials", "true");

        // ✅ 放行预检请求
        if("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        // ✅ 白名单放行
        String path = request.getRequestURI();
        for(String url : WHITELIST) {
            if(path.startsWith(url)) {
                chain.doFilter(request, response);
                return;
            }
        }

        // ✅ 检查JWT Token
        String header = request.getHeader("Authorization");
        if(header == null || !header.startsWith("Bearer ")) {
            writeJson(response, HttpServletResponse.SC_UNAUTHORIZED, "Missing or invalid Authorization header");
            return;
        }

        String token = header.substring(7);
        try {
            if(!JwtUtil.validateToken(token)) {
                writeJson(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
                return;
            }

            // ✅ 从Token提取信息存入请求
            request.setAttribute("username", JwtUtil.getUsername(token));
            request.setAttribute("userId", JwtUtil.getClaim(token, "userId"));
            request.setAttribute("role", JwtUtil.getClaim(token, "role"));
            request.setAttribute("email", JwtUtil.getClaim(token, "email"));

        } catch(JwtException e) {
            writeJson(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
            return;
        }

        chain.doFilter(request, response);
    }

    /**
     * 输出统一的 JSON 错误响应
     */
    private void writeJson(HttpServletResponse response, int code, String msg) throws IOException {
        response.setStatus(code);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(String.format("{\"code\":%d,\"msg\":\"%s\"}", code, msg));
    }
}
