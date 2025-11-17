package com.example.quotation.service.impl;

import com.example.quotation.entity.PackageItem;
import com.example.quotation.entity.Product;
import com.example.quotation.mapper.PackageItemMapper;
import com.example.quotation.mapper.ProductMapper;
import com.example.quotation.service.PackageItemService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

@Service
public class PackageItemServiceImpl implements PackageItemService {
    
    @Resource
    private PackageItemMapper packageItemMapper;
    @Resource
    private ProductMapper productMapper;
    
    @Override
    public List<PackageItem> listByPackageId(Long packageId) {
        QueryWrapper<PackageItem> qw = new QueryWrapper<>();
        qw.eq("package_id", packageId);
        qw.orderByAsc("id");
        List<PackageItem> items = packageItemMapper.selectList(qw);
        
        // 填充产品信息
        for (PackageItem item : items) {
            if (item.getProductId() != null) {
                Product product = productMapper.selectById(item.getProductId());
                if (product != null) {
                    item.setProductName(product.getName());
                    item.setProductSku(product.getSku());
                }
            }
        }
        
        return items;
    }
    
    @Override
    public PackageItem save(PackageItem it) {
        if (it.getId() == null) {
            packageItemMapper.insert(it);
        } else {
            packageItemMapper.updateById(it);
        }
        return it;
    }
    
    @Override
    public void delete(Long id) {
        packageItemMapper.deleteById(id);
    }
}
