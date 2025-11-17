package com.example.quotation.service.impl;

import com.example.quotation.entity.Attachment;
import com.example.quotation.mapper.AttachmentMapper;
import com.example.quotation.service.AttachmentService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

@Service
public class AttachmentServiceImpl implements AttachmentService {
    
    @Resource
    private AttachmentMapper attachmentMapper;
    
    @Override
    public List<Attachment> findByQuotationId(Long quotationId) {
        QueryWrapper<Attachment> qw = new QueryWrapper<>();
        qw.eq("quotation_id", quotationId);
        qw.orderByDesc("uploaded_at");
        return attachmentMapper.selectList(qw);
    }
    
    @Override
    public Attachment findById(Long id) {
        return attachmentMapper.selectById(id);
    }
    
    @Override
    public Attachment save(Attachment attachment) {
        if (attachment.getUploadedAt() == null) {
            attachment.setUploadedAt(LocalDateTime.now());
        }
        if (attachment.getId() == null) {
            attachmentMapper.insert(attachment);
        } else {
            attachmentMapper.updateById(attachment);
        }
        return attachment;
    }
    
    @Override
    public void delete(Long id) {
        attachmentMapper.deleteById(id);
    }
}

