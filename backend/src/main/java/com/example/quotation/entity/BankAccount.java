package com.example.quotation.entity;

import lombok.Data;

@Data
public class BankAccount {
    /**
     * 收款人
     */
    private String payee;
    /**
     * 收款账号名称
     */
    private String bankName;
    /**
     * 账户号码
     */
    private String accountNumber;

}
