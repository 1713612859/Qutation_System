package com.example.quotation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.quotation.entity.ApprovalRecord;
import com.example.quotation.mapper.ApprovalRecordMapper;
import com.example.quotation.service.ApprovalRecordService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ApprovalRecordServiceImpl implements ApprovalRecordService {

    @Resource
    private ApprovalRecordMapper approvalRecordMapper;

    @Override
    public List<ApprovalRecord> findByQuotationId(Long quotationId) {
        QueryWrapper<ApprovalRecord> qw = new QueryWrapper<>();
        qw.eq("quotation_id", quotationId);
        qw.orderByDesc("action_time");
        return approvalRecordMapper.selectList(qw);
    }

    @Override
    public ApprovalRecord save(ApprovalRecord record) {
        if(record.getActionTime() == null) {
            record.setActionTime(LocalDateTime.now());
        }
        approvalRecordMapper.insert(record);
        return record;
    }

    @Override
    public ApprovalRecord selectApprovedRecord(Long quotationId) {
        return approvalRecordMapper.selectOne(new QueryWrapper<ApprovalRecord>()
                .eq("quotation_id", quotationId)
                .eq("action", "APPROVE"));
    }
}

