package com.example.quotation.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@TableName("customers")
@Data
public class Customer {
    @TableId
    private Long id;
    /**
     * 公司名称
     */
    private String companyName;
    //@Version(1.1)
    /**
     * 客户名称
     */
    private String customerName;
    /**
     * 联系人
     */
    private String contactName;
    /**
     * 手机
     */
    private String contactPhone;
    /**
     * 邮箱
     */
    private String contactEmail;
    /**
     * 地址
     */
    private String address;
    /**
     * 城市
     */
    private String city;
    /**
     * 省份
     */
    private String province;
    /**
     * 国家
     */
    private String country;
    /**
     * 邮编
     */
    private String postalCode;
    /**
     * 税号
     */
    private String taxId;
    /**
     * 备注
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
     * 配送地址
     */
    private String shippingAddress;
}

