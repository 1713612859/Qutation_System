package com.example.quotation.service.impl;

import com.example.quotation.entity.SystemSettings;
import com.example.quotation.mapper.SystemSettingsMapper;
import com.example.quotation.service.SystemSettingsService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

@Service
public class SystemSettingsServiceImpl implements SystemSettingsService {
    
    @Resource
    private SystemSettingsMapper systemSettingsMapper;
    
    @Override
    public String getSetting(String key) {
        QueryWrapper<SystemSettings> qw = new QueryWrapper<>();
        qw.eq("setting_key", key);
        SystemSettings setting = systemSettingsMapper.selectOne(qw);
        return setting != null ? setting.getSettingValue() : null;
    }
    
    @Override
    public void setSetting(String key, String value) {
        QueryWrapper<SystemSettings> qw = new QueryWrapper<>();
        qw.eq("setting_key", key);
        SystemSettings existing = systemSettingsMapper.selectOne(qw);
        
        if (existing != null) {
            existing.setSettingValue(value);
            existing.setUpdatedAt(LocalDateTime.now());
            systemSettingsMapper.updateById(existing);
        } else {
            SystemSettings setting = new SystemSettings();
            setting.setSettingKey(key);
            setting.setSettingValue(value);
            setting.setUpdatedAt(LocalDateTime.now());
            systemSettingsMapper.insert(setting);
        }
    }
    
    @Override
    public Map<String, String> getAllSettings() {
        List<SystemSettings> settings = systemSettingsMapper.selectList(null);
        Map<String, String> result = new HashMap<>();
        for (SystemSettings s : settings) {
            result.put(s.getSettingKey(), s.getSettingValue());
        }
        return result;
    }
    
    @Override
    public List<SystemSettings> listAll() {
        return systemSettingsMapper.selectList(null);
    }
}

