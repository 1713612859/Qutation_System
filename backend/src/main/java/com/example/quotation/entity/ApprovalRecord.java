package com.example.quotation.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@TableName("approval_records")
@Data
public class ApprovalRecord {
    @TableId
    private Long id;
    private Long quotationId;
    private Long approverId;
    private String approverName;
    private String action; // SUBMIT, APPROVE, REJECT, CANCEL
    private String comment;
    private LocalDateTime actionTime;
}

