package com.example.quotation.service;

import com.example.quotation.entity.Quotation;

public interface PdfExportService {
    byte[] exportQuotationToPdf(Quotation quotation) throws Exception;
}

