package com.example.quotation.service;

import com.example.quotation.entity.Quotation;
import com.example.quotation.entity.QuotationItem;

import java.util.List;

public interface QuotationService {
    Quotation save(Quotation q);

    Quotation createFromPackage(Long packageId, Long customerId, Long userId);

    Quotation submit(Long id, Long userId);

    Quotation approve(Long id, Long approverId, String comment);

    Quotation reject(Long id, Long approverId, String comment);

    List<Quotation> listAll();

    List<Quotation> listByStatus(String status);

    List<Quotation> listByCustomerId(Long customerId);

    Quotation findById(Long id);

    List<QuotationItem> listItems(Long quotationId);

    void calculateTotal(Quotation quotation);

    void delete(Long id);

    List<Quotation> listByUserId(Long id);
}
