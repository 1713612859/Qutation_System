package com.example.quotation.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.quotation.entity.PackageEntity;
import com.example.quotation.entity.PackageItem;
import com.example.quotation.entity.Product;
import com.example.quotation.mapper.PackageItemMapper;
import com.example.quotation.mapper.PackageMapper;
import com.example.quotation.mapper.ProductMapper;
import com.example.quotation.service.PackageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 套餐服务实现类
 * 负责套餐的CRUD操作和产品列表管理
 *
 * @author System
 */
@Service
public class PackageServiceImpl implements PackageService {

    @Resource
    private PackageMapper packageMapper;

    @Resource
    private PackageItemMapper packageItemMapper;

    @Resource
    private ProductMapper productMapper;

    @Override
    public List<PackageEntity> listAll() {
        return packageMapper.selectList(null);
    }

    @Override
    public List<PackageEntity> listEnabled() {
        QueryWrapper<PackageEntity> qw = new QueryWrapper<>();
        qw.eq("enabled", true);
        return packageMapper.selectList(qw);
    }

    @Override
    public PackageEntity findById(Long id) {
        PackageEntity pkg = packageMapper.selectById(id);
        if(pkg != null) {
            // 加载产品列表
            QueryWrapper<PackageItem> qw = new QueryWrapper<>();
            qw.eq("package_id", id);
            List<PackageItem> items = packageItemMapper.selectList(qw);
            // 填充产品信息
            for(PackageItem item : items) {
                if(item.getProductId() != null) {
                    Product product = productMapper.selectById(item.getProductId());
                    if(product != null) {
                        item.setProductName(product.getName());
                        item.setProductSku(product.getSku());
                        // TODO : Maybe have others detail need save to  item detail
                    }
                }
            }
            pkg.setItems(items);
        }
        return pkg;
    }

    /**
     * 保存套餐（新增或更新）
     * 同时保存套餐的产品列表
     *
     * @param p 套餐对象（包含items列表）
     * @return 保存后的套餐对象
     */
    @Override
    @Transactional
    public PackageEntity save(PackageEntity p) {
        if(p.getId() == null) {
            // 新增套餐
            p.setCreatedAt(LocalDateTime.now());
            p.setEnabled(Optional.ofNullable(p.getEnabled()).orElse(true));
            packageMapper.insert(p);
        } else {
            // 更新套餐
            p.setUpdatedAt(LocalDateTime.now());
            packageMapper.updateById(p);

            // 删除旧的套餐明细
            QueryWrapper<PackageItem> qw = new QueryWrapper<>();
            qw.eq("package_id", p.getId());
            packageItemMapper.delete(qw);
        }

        // 保存套餐产品列表
        if(CollUtil.isNotEmpty(p.getItems())) {
            for(PackageItem item : p.getItems()) {
                item.setPackageId(p.getId());
                // 填充产品信息
                if(ObjectUtil.isNotNull(item.getProductId())) {
                    Product product = productMapper.selectById(item.getProductId());
                    if(ObjectUtil.isNotNull(product)) {
                        item.setProductName(product.getName());
                        item.setProductSku(product.getSku());
                    }
                }
                packageItemMapper.insert(item);
            }
        }

        // 重新加载并返回（包含产品列表）
        return findById(p.getId());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        // 删除套餐明细（级联删除）
        QueryWrapper<PackageItem> qw = new QueryWrapper<>();
        qw.eq("package_id", id);
        packageItemMapper.delete(qw);
        // 删除套餐
        packageMapper.deleteById(id);
    }
}
