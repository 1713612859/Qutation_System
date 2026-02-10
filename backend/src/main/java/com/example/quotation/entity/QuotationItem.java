
package com.example.quotation.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 报价单明细项实体类
 * 对应数据库表：quotation_items
 */
@TableName("quotation_items")
@Data
public class QuotationItem {
    /**
     * 主键ID
     */
    @TableId
    private Long id;

    /**
     * 报价单ID，关联主报价单
     */
    private Long quotationId;

    /**
     * 行号，表示在报价单中的显示顺序
     */
    private Integer lineNumber;

    /**
     * 产品ID，关联产品表
     */
    private Long productId;

    /**
     * 产品名称
     */
    private String productName;

    /**
     * 套餐ID，如果是套餐产品则关联套餐表
     */
    private Long packageId;

    /**
     * 套餐名称
     */
    private String packageName;

    /**
     * 产品SKU编码，唯一标识产品规格
     */
    private String productSku;

    /**
     * 产品或服务的详细描述说明
     */
    private String description;

    /**
     * 数量
     */
    private BigDecimal quantity;

    /**
     * 计量单位（如：个、台、套、米等）
     */
    private String unit;

    /**
     * 单价
     */
    private BigDecimal unitPrice;

    /**
     * 折扣，可以是百分比或固定金额
     */
    private BigDecimal discount;

    /**
     * 折扣后的实际折扣金额
     */
    private BigDecimal discountAmount;

    /**
     * 税率（如：0.12表示12%的税率）
     */
    private BigDecimal taxRate;

    /**
     * 行小计（折扣前金额 = 数量 × 单价）
     */
    private BigDecimal lineSubtotal;

    /**
     * 行税额（税金 = 行小计 × 税率）
     */
    private BigDecimal lineTax;

    /**
     * 行总计（含税总额 = 行小计 - 折扣金额 + 行税额）
     */
    private BigDecimal lineTotal;

    /**
     * ewt 商品行金额
     */
    @TableField(value = "line_ewt")
    private BigDecimal lineEWT;
}
