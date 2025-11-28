
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
 * 用于管理产品分类的层级结构，支持父子级关系
 *
 * @author System
 * @since 1.0
 */
@Data
@TableName("categories")
public class Category implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 分类主键ID，自增长
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 父级分类ID，若为null或0则表示顶级分类
     */
    private Integer parentId;

    /**
     * 分类描述信息
     */
    private String description;

    /**
     * 排序顺序，数值越小越靠前
     */
    private Integer sortOrder;

    /**
     * 是否启用，true为启用，false为禁用
     */
    @TableField("is_enabled")
    private Boolean enabled;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 子分类列表，用于构建树形结构
     * 该字段不映射到数据库表
     */
    @TableField(exist = false)
    private List<Category> children;
}
