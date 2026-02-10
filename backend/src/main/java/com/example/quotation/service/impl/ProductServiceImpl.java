package com.example.quotation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.quotation.entity.Product;
import com.example.quotation.entity.QuotationItem;
import com.example.quotation.exception.ServiceException;
import com.example.quotation.mapper.ProductMapper;
import com.example.quotation.mapper.QuotationItemMapper;
import com.example.quotation.service.ProductService;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Resource
    private ProductMapper productMapper;
    @Resource
    private QuotationItemMapper quotationItemMapper;

    @Override
    public List<Product> listAll() {

        QueryWrapper<Product> qw = new QueryWrapper<>();
        qw.orderByAsc("id");
        List<Product> products = productMapper.selectList(qw);
        return getProducts(products);
    }

    @NonNull
    private List<Product> getProducts(List<Product> products) {
        products.forEach(item -> {
            // 获取税率，默认为0
            BigDecimal taxRate = item.getTaxRate() != null ? item.getTaxRate() : BigDecimal.ZERO;
            // 计算未税价
            if (item.getDefaultPrice() != null) {
                // 未税价 = 含税价 / (1 + 税率)
                BigDecimal excludingTaxPrice = item.getDefaultPrice().divide(
                        BigDecimal.ONE.add(taxRate),
                        2,
                        BigDecimal.ROUND_HALF_UP
                );
                item.setExcludingTaxPrice(excludingTaxPrice);
            }
        });
        return products;
    }

    @Override
    public List<Product> listEnabled() {
        QueryWrapper<Product> qw = new QueryWrapper<>();
        qw.eq("enabled", true);
        List<Product> products = productMapper.selectList(qw);


        return getProducts(products);
    }

    @Override
    public Product findById(Long id) {
        return productMapper.selectById(id);
    }

    @Override
    public Product save(Product p) {
        if(p.getId() == null) {
            // ✅ 新增
            String sku = p.getSku();
            if(sku == null || sku.trim().isEmpty()) {
                throw new ServiceException("SKU cannot be empty");
            }

            QueryWrapper<Product> qw = new QueryWrapper<>();
            qw.eq("sku", sku.trim().toUpperCase());
            if(productMapper.selectCount(qw) > 0) {
                throw new ServiceException("SKU already exist");
            }

            p.setSku(sku.trim().toUpperCase());
            p.setCreatedAt(LocalDateTime.now());
            if(p.getEnabled() == null) {
                p.setEnabled(true);
            }
            productMapper.insert(p);
        } else {
            // ✅ 修改
            Product existing = productMapper.selectById(p.getId());
            if(existing == null) {
                throw new ServiceException("Product does not exist");
            }

            // 如果用户修改了 SKU，检查是否冲突
            if(!existing.getSku().equalsIgnoreCase(p.getSku())) {
                QueryWrapper<Product> qw = new QueryWrapper<>();
                qw.eq("sku", p.getSku().trim().toUpperCase());
                if(productMapper.selectCount(qw) > 0) {
                    throw new ServiceException("SKU already exists, cannot modify to duplicate SKU");
                }
                p.setSku(p.getSku().trim().toUpperCase());
            } else {
                // 不修改 SKU
                p.setSku(existing.getSku());
            }

            p.setUpdatedAt(LocalDateTime.now());
            productMapper.updateById(p);
        }
        return p;
    }


    @Override
    public void delete(Long id) {
        Product existing = productMapper.selectById(id);
        if(existing == null) {
            throw new ServiceException("Product does not exist");
        }
        Boolean enabled = existing.getEnabled();
        if(enabled != null && enabled) {
            throw new ServiceException("Product is enabled and cannot be deleted");
        }
        QueryWrapper<QuotationItem> qw = new QueryWrapper<>();
        qw.eq("product_id", id);
        if(quotationItemMapper.selectCount(qw) > 0) {
            throw new ServiceException("Product has been used and cannot be deleted");
        }
        productMapper.deleteById(id);
    }
}
