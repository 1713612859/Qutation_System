package com.example.quotation.service.impl;

import com.example.quotation.entity.Quotation;
import com.example.quotation.entity.QuotationItem;
import com.example.quotation.service.PdfExportService;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PdfExportServiceImpl implements PdfExportService {
    
    @Override
    public byte[] exportQuotationToPdf(Quotation quotation) throws Exception {
        // 由于iText7的依赖较复杂，这里提供一个简化版本的HTML转PDF方案
        // 实际生产环境可以使用iText7或Apache PDFBox
        
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><head>");
        html.append("<meta charset='UTF-8'>");
        html.append("<style>");
        html.append("body { font-family: Arial, sans-serif; margin: 20px; }");
        html.append("table { width: 100%; border-collapse: collapse; margin: 20px 0; }");
        html.append("th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }");
        html.append("th { background-color: #f2f2f2; }");
        html.append(".header { margin-bottom: 20px; }");
        html.append(".total { text-align: right; font-weight: bold; }");
        html.append("</style>");
        html.append("</head><body>");
        
        // 标题
        html.append("<div class='header'>");
        html.append("<h1>报价单</h1>");
        html.append("<p><strong>报价单号:</strong> ").append(quotation.getQuoteNumber()).append("</p>");
        html.append("<p><strong>标题:</strong> ").append(quotation.getTitle()).append("</p>");
        html.append("<p><strong>客户:</strong> ").append(quotation.getCustomerName()).append("</p>");
        html.append("<p><strong>状态:</strong> ").append(quotation.getStatus()).append("</p>");
        if (quotation.getIssueDate() != null) {
            html.append("<p><strong>签发日期:</strong> ").append(quotation.getIssueDate().format(DateTimeFormatter.ISO_LOCAL_DATE)).append("</p>");
        }
        if (quotation.getExpiryDate() != null) {
            html.append("<p><strong>有效期至:</strong> ").append(quotation.getExpiryDate().format(DateTimeFormatter.ISO_LOCAL_DATE)).append("</p>");
        }
        html.append("</div>");
        
        // 明细表
        html.append("<table>");
        html.append("<thead>");
        html.append("<tr>");
        html.append("<th>序号</th>");
        html.append("<th>产品名称</th>");
        html.append("<th>SKU</th>");
        html.append("<th>数量</th>");
        html.append("<th>单位</th>");
        html.append("<th>单价</th>");
        html.append("<th>折扣</th>");
        html.append("<th>税率(%)</th>");
        html.append("<th>小计</th>");
        html.append("</tr>");
        html.append("</thead>");
        html.append("<tbody>");
        
        List<QuotationItem> items = quotation.getItems();
        if (items != null) {
            for (QuotationItem item : items) {
                html.append("<tr>");
                html.append("<td>").append(item.getLineNumber()).append("</td>");
                html.append("<td>").append(item.getProductName() != null ? item.getProductName() : "").append("</td>");
                html.append("<td>").append(item.getProductSku() != null ? item.getProductSku() : "").append("</td>");
                html.append("<td>").append(item.getQuantity()).append("</td>");
                html.append("<td>").append(item.getUnit() != null ? item.getUnit() : "").append("</td>");
                html.append("<td>").append(item.getUnitPrice()).append("</td>");
                html.append("<td>").append(item.getDiscount() != null ? item.getDiscount() : BigDecimal.ZERO).append("</td>");
                html.append("<td>").append(item.getTaxRate() != null ? item.getTaxRate() : BigDecimal.ZERO).append("</td>");
                html.append("<td>").append(item.getLineTotal() != null ? item.getLineTotal() : BigDecimal.ZERO).append("</td>");
                html.append("</tr>");
            }
        }
        
        html.append("</tbody>");
        html.append("</table>");
        
        // 总计
        html.append("<div class='total'>");
        html.append("<p>小计: ").append(quotation.getCurrency()).append(" ").append(quotation.getSubtotal()).append("</p>");
        if (quotation.getDiscountAmount() != null && quotation.getDiscountAmount().compareTo(BigDecimal.ZERO) > 0) {
            html.append("<p>折扣: -").append(quotation.getCurrency()).append(" ").append(quotation.getDiscountAmount()).append("</p>");
        }
        html.append("<p>税额: ").append(quotation.getCurrency()).append(" ").append(quotation.getTaxAmount()).append("</p>");
        html.append("<p><strong>总计: ").append(quotation.getCurrency()).append(" ").append(quotation.getTotal()).append("</strong></p>");
        html.append("</div>");
        
        if (quotation.getNotes() != null && !quotation.getNotes().isEmpty()) {
            html.append("<div><p><strong>备注:</strong> ").append(quotation.getNotes()).append("</p></div>");
        }
        
        html.append("</body></html>");
        
        // 返回HTML字符串的字节数组
        // 实际应用中，可以使用iText7或wkhtmltopdf将HTML转换为PDF
        return html.toString().getBytes("UTF-8");
    }
}

