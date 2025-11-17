package com.example.quotation.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 报价单实体类
 * 用于存储报价单的基本信息和金额汇总
 *
 * @author System
 */
@TableName("quotations")
@Data
public class Quotation {
    /**
     * 报价单ID，主键，自增
     */
    @TableId
    private Long id;

    /**
     * 报价单号，唯一标识，格式：QT + 日期 + 序列号
     */
    private String quoteNumber;

    /**
     * 报价单标题/名称
     */
    private String title;

    /**
     * 客户ID，外键关联customers表
     */
    private Long customerId;

    /**
     * 客户名称（冗余字段，便于查询显示）
     */
    private String customerName;

    /**
     * 创建人ID，外键关联users表
     */
    private Long createdBy;

    /**
     * 创建人姓名（冗余字段）
     */
    private String createdByName;

    /**
     * 货币单位，默认CNY
     */
    private String currency;

    /**
     * 小计金额（不含税，已扣除行级折扣）
     */
    private BigDecimal subtotal;

    /**
     * 报价单级别折扣金额（报价单总折扣）
     */
    private BigDecimal discountAmount;

    /**
     * 税额总计
     */
    private BigDecimal taxAmount;

    /**
     * 总金额（小计 + 税额 - 报价单折扣）
     */
    private BigDecimal total;

    /**
     * 报价单状态
     * DRAFT - 草稿（可编辑）
     * SUBMITTED - 已提交（等待审批）
     * APPROVED - 已审批通过（可导出PDF）
     * REJECTED - 已拒绝（可重新编辑后再次提交）
     * CANCELLED - 已取消
     */
    private String status;

    /**
     * 报价单签发日期
     */
    private LocalDate issueDate;

    /**
     * 报价单有效期至
     */
    private LocalDate expiryDate;

    /**
     * 套餐ID（如果是从套餐创建的），外键关联packages表
     */

    private Long packageId;

    /**
     * 备注信息
     */
    private String notes;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;


    /**
     * 操作人
     */
    private String operator;

    /**
     * 敬称
     */
    private String salutation;

    /**
     * 报价明细项列表（非数据库字段，用于业务逻辑处理）
     * 包含报价单的所有产品明细
     */
    @TableField(exist = false)
    private List<QuotationItem> items;
}
