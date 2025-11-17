package com.example.quotation.controller;

import com.example.quotation.common.ApiResponse;
import com.example.quotation.entity.ApprovalRecord;
import com.example.quotation.service.ApprovalRecordService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/approval-records")
public class ApprovalRecordController {

    @Resource
    private ApprovalRecordService approvalRecordService;

    /**
     * 获取审批记录
     *
     * @param quotationId
     * @return
     */
    @GetMapping("/quotation/{quotationId}")
    public ApiResponse<List<ApprovalRecord>> getByQuotationId(@PathVariable Long quotationId) {
        return ApiResponse.ok(approvalRecordService.findByQuotationId(quotationId));
    }

    /**
     * 获取通过审批的记录
     *
     * @param quotationId
     * @return
     */
    @GetMapping("/quotation/{quotationId}/approved")
    public ApiResponse<ApprovalRecord> getApprovedRecord(@PathVariable Long quotationId) {
        ApprovalRecord approvalRecord = approvalRecordService.selectApprovedRecord(quotationId);
        if(approvalRecord == null) {
            return ApiResponse.error("Can not find quotation id : " + quotationId);
        }
        return ApiResponse.ok(approvalRecord);
    }
}

