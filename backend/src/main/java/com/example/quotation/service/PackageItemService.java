package com.example.quotation.service;

import com.example.quotation.entity.PackageItem;
import java.util.List;

public interface PackageItemService {
    List<PackageItem> listByPackageId(Long packageId);
    PackageItem save(PackageItem it);
    void delete(Long id);
}
