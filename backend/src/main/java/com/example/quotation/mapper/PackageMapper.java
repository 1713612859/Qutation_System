package com.example.quotation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.quotation.entity.PackageEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 套餐映射器
 */
@Mapper
public interface PackageMapper extends BaseMapper<PackageEntity> {
}