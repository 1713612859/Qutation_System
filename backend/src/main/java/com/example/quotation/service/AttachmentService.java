package com.example.quotation.service;

import com.example.quotation.entity.Attachment;
import java.util.List;

public interface AttachmentService {
    List<Attachment> findByQuotationId(Long quotationId);
    Attachment findById(Long id);
    Attachment save(Attachment attachment);
    void delete(Long id);
}

