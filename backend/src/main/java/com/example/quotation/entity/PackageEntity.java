package com.example.quotation.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 套餐实体类
 * 用于存储套餐的基本信息和产品列表
 *
 * @author System
 */
@TableName("packages")
@Data
public class PackageEntity {
    /**
     * 套餐ID，主键，自增
     */
    @TableId
    private Long id;

    /**
     * 套餐代码，唯一标识
     */
    private String code;

    /**
     * 套餐名称
     */
    private String name;

    /**
     * 套餐描述
     */
    private String description;

    /**
     * 货币单位
     */
    private String currency;

    /**
     * 套餐整体折扣（百分比，如0.1表示10%折扣）
     */
    private BigDecimal discount;

    /**
     * 套餐默认税率（百分比，如13表示13%）
     */
    @TableField("tax_rate")
    private BigDecimal taxRate;

    /**
     * 是否启用
     */
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
     * 套餐产品列表（非数据库字段，用于业务逻辑处理）
     * 包含套餐的所有产品明细
     */
    @TableField(exist = false)
    private List<PackageItem> items;
}
