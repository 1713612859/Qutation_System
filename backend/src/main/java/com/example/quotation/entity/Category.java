package com.example.quotation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 分类实体类 (对应 categories 表)
 */
@Data
@TableName("categories")
public class Category implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String name;

    private Integer parentId; // 父级分类ID

    private String description;

    private Integer sortOrder;

    @TableField("is_enabled")
    private Boolean enabled;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // 额外属性：用于存储子分类 (用于树形结构)
    @TableField(exist = false)
    private List<Category> children;
}
