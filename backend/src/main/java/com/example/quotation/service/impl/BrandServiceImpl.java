package com.example.quotation.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.quotation.entity.Brand;
import com.example.quotation.mapper.BrandMapper;
import com.example.quotation.service.BrandService;
import org.springframework.stereotype.Service;

@Service
public class BrandServiceImpl extends ServiceImpl<BrandMapper, Brand> implements BrandService {
    // 品牌业务逻辑实现
}