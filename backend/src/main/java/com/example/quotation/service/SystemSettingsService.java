package com.example.quotation.service;

import com.example.quotation.entity.SystemSettings;

import java.util.List;
import java.util.Map;

public interface SystemSettingsService {
    String getSetting(String key);

    void setSetting(String key, String value);

    Map<String, String> getAllSettings();

    List<SystemSettings> listAll();
}

