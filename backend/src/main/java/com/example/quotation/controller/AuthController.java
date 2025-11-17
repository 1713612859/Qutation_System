package com.example.quotation.controller;

import com.example.quotation.common.ApiResponse;
import com.example.quotation.entity.User;
import com.example.quotation.service.UserService;
import com.example.quotation.util.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 * 处理用户登录、注册、获取当前用户信息等认证相关功能
 *
 * @author System
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Resource
    private UserService userService;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 用户登录
     * 逻辑：
     * 1. 验证用户名和密码
     * 2. 使用BCrypt验证密码
     * 3. 检查用户是否启用
     * 4. 生成JWT Token并返回用户信息
     *
     * @param body 包含username和password的请求体
     * @return 包含token和用户信息的响应
     */
    @PostMapping("/login")
    public ApiResponse<Map<String, Object>> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        // 验证参数
        if(username == null || password == null) {
            return ApiResponse.error("Username and password cannot be empty");
        }

        // 查询用户
        User user = userService.findByUsername(username);
        if(user == null) {
            return ApiResponse.error("Invalid username or password");
        }

        // 验证密码（BCrypt加密匹配）
        if(!passwordEncoder.matches(password, user.getPassword())) {
            return ApiResponse.error("Invalid username or password");
        }

        // 检查用户是否启用
        if(user.getEnabled() == null || !user.getEnabled()) {
            return ApiResponse.error("User has been disabled");
        }

        // 生成JWT Token
        String token = JwtUtil.generateToken(user.getId(), username, user.getRole(), user.getEmail());
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("username", username);
        data.put("fullName", user.getFullName());
        data.put("role", user.getRole());
        data.put("userId", user.getId());

        return ApiResponse.ok(data);
    }

    /**
     * 用户注册
     * 逻辑：
     * 1. 检查用户名是否已存在
     * 2. 密码使用BCrypt加密存储
     * 3. 默认角色为SALES
     * 4. 生成JWT Token并返回用户信息
     *
     * @param user 用户对象（包含username、password等信息）
     * @return 包含token和用户信息的响应
     */
    @PostMapping("/register")
    public ApiResponse<Map<String, Object>> register(@RequestBody User user) {
        try {
            // 注册用户（内部会检查用户名是否已存在）
            User newUser = userService.register(user);
            // 生成JWT Token
            String token = JwtUtil.generateToken(newUser.getId(), newUser.getUsername(), newUser.getRole(), newUser.getEmail());
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("username", newUser.getUsername());
            data.put("fullName", newUser.getFullName());
            data.put("role", newUser.getRole());
            data.put("userId", newUser.getId());
            return ApiResponse.ok(data);
        } catch(Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取当前登录用户信息
     * 从JWT Token中解析用户名，查询用户信息
     * 注意：密码字段会被清除，不会返回给前端
     *
     * @param request HTTP请求对象，JWT Filter会将username存储在request attribute中
     * @return 当前用户信息（不含密码）
     */
    @GetMapping("/me")
    public ApiResponse<User> getCurrentUser(HttpServletRequest request) {
        // 从JWT Filter中获取用户名
        String username = (String) request.getAttribute("username");
        if(username == null) {
            return ApiResponse.error("Not Login");
        }
        User user = userService.findByUsername(username);
        if(user == null) {
            return ApiResponse.error("User not found");
        }
        // 清除密码，不返回给前端
        user.setPassword(null);
        return ApiResponse.ok(user);
    }
}
