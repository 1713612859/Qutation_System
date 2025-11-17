package com.example.quotation.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@TableName("quotation_items")
@Data
public class QuotationItem {
    @TableId
    private Long id;
    private Long quotationId;
    private Integer lineNumber;
    private Long productId;
    private String productName;
    private Long packageId;
    private String packageName;
    private String productSku;
    private String description;
    private BigDecimal quantity;
    private String unit;
    private BigDecimal unitPrice;
    private BigDecimal discount; // 折扣百分比或金额
    private BigDecimal discountAmount;
    private BigDecimal taxRate;
    private BigDecimal lineSubtotal;
    private BigDecimal lineTax;
    private BigDecimal lineTotal;
}
