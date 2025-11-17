package com.example.quotation.service;

import com.example.quotation.entity.ApprovalRecord;
import java.util.List;

public interface ApprovalRecordService {
    List<ApprovalRecord> findByQuotationId(Long quotationId);
    ApprovalRecord save(ApprovalRecord record);

    ApprovalRecord selectApprovedRecord(Long quotationId);
}

