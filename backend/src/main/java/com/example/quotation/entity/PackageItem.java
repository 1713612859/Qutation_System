
package com.example.quotation.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 套餐明细项
 */
@TableName("package_items")
@Data
public class PackageItem {
    /**
     * 主键ID
     */
    @TableId
    private Long id;
    /**
     * 套餐ID
     */
    private Long packageId;
    /**
     * 产品ID
     */
    private Long productId;
    /**
     * 产品名称
     */
    private String productName;
    /**
     * 产品SKU
     */
    private String productSku;
    /**
     * 数量
     */
    private BigDecimal quantity;
    /**
     * 单价
     */
    private BigDecimal unitPrice;
    /**
     * 折扣
     */
    private BigDecimal discount;
    /**
     * 税率
     */
    private BigDecimal taxRate;
    /**
     * 备注
     */
    private String note;
}
