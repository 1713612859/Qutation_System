
package com.example.quotation.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统设置实体类
 * 用于存储系统配置参数
 */
@TableName("system_settings")
@Data
public class SystemSettings {
    /**
     * 主键ID
     */
    @TableId
    private Long id;
    
    /**
     * 配置项键名
     */
    private String settingKey;
    
    /**
     * 配置项值
     */
    private String settingValue;
    
    /**
     * 配置项描述说明
     */
    private String description;
    
    /**
     * 最后更新时间
     */
    private LocalDateTime updatedAt;
}

