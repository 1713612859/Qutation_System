package com.example.quotation.controller;

import com.example.quotation.common.ApiResponse;
import com.example.quotation.entity.User;
import com.example.quotation.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @Resource
    private UserService userService;
    
    @GetMapping
    public ApiResponse<List<User>> list() {
        List<User> users = userService.listAll();
        // 清除密码
        users.forEach(u -> u.setPassword(null));
        return ApiResponse.ok(users);
    }
    
    @GetMapping("/{id}")
    public ApiResponse<User> get(@PathVariable Long id) {
        User user = userService.findById(id);
        if (user == null) {
            return ApiResponse.error("User not found");
        }
        user.setPassword(null);
        return ApiResponse.ok(user);
    }
    
    @PostMapping
    public ApiResponse<User> save(@RequestBody User user) {
        user = userService.save(user);
        user.setPassword(null);
        return ApiResponse.ok(user);
    }
    
    @PutMapping("/{id}")
    public ApiResponse<User> update(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        user = userService.update(user);
        user.setPassword(null);
        return ApiResponse.ok(user);
    }
    
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ApiResponse.ok(null);
    }
}

