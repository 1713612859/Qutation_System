package com.example.quotation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.quotation.entity.Category;

import java.util.List;

public interface CategoryService extends IService<Category> {
    /**
     * 获取分类列表，并构建成树形结构
     *
     * @return 包含子分类的根分类列表
     */
    List<Category> listWithTreeStructure();

    boolean hasChildren(Integer id);

    boolean hasProducts(Integer id);
}
