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
 * Brand entity class mapping to the brands table in database
 * Used for managing product brand information in the quotation system
 */
@Data
@TableName("brands")
public class Brand implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 品牌ID (主键，自增)
     * Brand ID (primary key, auto-increment)
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 品牌名称
     * Brand name
     */
    private String name;

    /**
     * 品牌描述信息
     * Brand description
     */
    private String description;

    /**
     * 是否启用 (true=启用, false=禁用)
     * Enable status (true=enabled, false=disabled)
     * Maps to is_enabled column in database
     */
    @TableField("is_enabled")
    private Boolean enabled;

    /**
     * 创建时间
     * Creation timestamp
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     * Last update timestamp
     */
    private LocalDateTime updatedAt;
}