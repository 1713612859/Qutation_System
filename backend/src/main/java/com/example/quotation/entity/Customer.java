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
    private String companyName;
    //@Version(1.1)
    private String customerName;
    private String contactName;
    private String contactPhone;
    private String contactEmail;
    private String address;
    private String city;
    private String province;
    private String country;
    private String postalCode;
    private String taxId;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

