package com.example.quotation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 品牌实体类 (对应 brands 表)
 */
@Data
@TableName("brands")
public class Brand implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String name;

    private String description;

    @TableField("is_enabled") // 对应数据库 is_enabled 字段
    private Boolean enabled;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}