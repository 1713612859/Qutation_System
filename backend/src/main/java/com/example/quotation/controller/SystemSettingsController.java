package com.example.quotation.controller;

import com.example.quotation.common.ApiResponse;
import com.example.quotation.entity.SystemSettings;
import com.example.quotation.service.SystemSettingsService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/settings")
public class SystemSettingsController {
    
    @Resource
    private SystemSettingsService systemSettingsService;
    
    @GetMapping
    public ApiResponse<Map<String, String>> getAll() {
        return ApiResponse.ok(systemSettingsService.getAllSettings());
    }
    
    @GetMapping("/list")
    public ApiResponse<List<SystemSettings>> list() {
        return ApiResponse.ok(systemSettingsService.listAll());
    }
    
    @GetMapping("/{key}")
    public ApiResponse<String> get(@PathVariable String key) {
        String value = systemSettingsService.getSetting(key);
        return ApiResponse.ok(value);
    }
    
    @PostMapping("/{key}")
    public ApiResponse<Void> set(@PathVariable String key, @RequestBody Map<String, String> body) {
        String value = body.get("value");
        systemSettingsService.setSetting(key, value);
        return ApiResponse.ok(null);
    }
}

