package com.example.quotation.controller;

import com.example.quotation.entity.Quotation;
import com.example.quotation.service.PdfExportService;
import com.example.quotation.service.QuotationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/api/pdf")
public class PdfExportController {

    @Resource
    private QuotationService quotationService;

    @Resource
    private PdfExportService pdfExportService;

    @GetMapping("/quotation/{id}")
    public void exportQuotation(@PathVariable Long id, HttpServletResponse response) throws IOException {
        Quotation quotation = quotationService.findById(id);
        if(quotation == null) {
            response.setStatus(404);
            return;
        }

        try {
            byte[] pdfBytes = pdfExportService.exportQuotationToPdf(quotation);

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + quotation.getQuoteNumber() + ".pdf\"");
            response.setContentLength(pdfBytes.length);

            response.getOutputStream().write(pdfBytes);
            response.getOutputStream().flush();
        } catch(Exception e) {
            response.setStatus(500);
            response.getWriter().write("PDF导出失败: " + e.getMessage());
        }
    }
}

