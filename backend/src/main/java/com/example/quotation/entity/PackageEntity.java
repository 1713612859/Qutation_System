
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
 * 对应数据库表：packages
 *
 * @author System
 */
@TableName("packages")
@Data
public class PackageEntity {
    /**
     * 套餐ID
     * 主键，自增
     * 数据库字段：id
     */
    @TableId
    private Long id;

    /**
     * 套餐代码
     * 唯一标识符，用于业务系统中识别套餐
     * 数据库字段：code
     */
    private String code;

    /**
     * 套餐名称
     * 套餐的显示名称
     * 数据库字段：name
     */
    private String name;

    /**
     * 套餐描述
     * 套餐的详细说明信息
     * 数据库字段：description
     */
    private String description;

    /**
     * 货币单位
     * 套餐价格使用的货币类型，如：CNY、USD等
     * 数据库字段：currency
     */
    private String currency;

    /**
     * 套餐整体折扣
     * 折扣率，以小数形式存储
     * 例如：0.1表示10%折扣，0.2表示20%折扣
     * 数据库字段：discount
     */
    private BigDecimal discount;

    /**
     * 套餐默认税率
     * 税率百分比值
     * 例如：13表示13%的税率，17表示17%的税率
     * 数据库字段：tax_rate
     */
    @TableField("tax_rate")
    private BigDecimal taxRate;

    /**
     * 是否启用
     * true表示套餐启用，false表示禁用
     * 禁用的套餐不会在前端展示
     * 数据库字段：enabled
     */
    private Boolean enabled;

    /**
     * 创建时间
     * 记录套餐首次创建的时间戳
     * 数据库字段：created_at
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     * 记录套餐最后一次更新的时间戳
     * 数据库字段：updated_at
     */
    private LocalDateTime updatedAt;

    /**
     * 套餐产品列表
     * 非数据库字段，仅用于业务逻辑处理
     * 包含该套餐下的所有产品明细项
     * 通过关联查询获取，用于前端展示和计算
     */
    @TableField(exist = false)
    private List<PackageItem> items;
}
