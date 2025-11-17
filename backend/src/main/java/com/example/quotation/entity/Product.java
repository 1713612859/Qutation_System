package com.example.quotation.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("products")
@Data
public class Product {
    @TableId
    private Long id;
    /**
     * 商品SKU
     */
    @TableField("sku")
    private String sku;
    /**
     * 商品名称
     */
    private String name;
    /**
     * 商品描述
     */
    private String description;
    /**
     * 单位
     */
    private String unit;
    /**
     * 默认价格
     */
    private BigDecimal defaultPrice;
    /**
     * 默认税率
     * <p>
     * 默认为0  0表示没有税率 ，0.12 表示12%
     */

    private BigDecimal taxRate; // 默认税率
    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 规格
     */
    private String  specifications;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
