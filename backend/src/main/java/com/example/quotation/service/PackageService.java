package com.example.quotation.service;

import com.example.quotation.entity.PackageEntity;
import java.util.List;

public interface PackageService {
    List<PackageEntity> listAll();
    List<PackageEntity> listEnabled();
    PackageEntity findById(Long id);
    PackageEntity save(PackageEntity p);
    void delete(Long id);
}
