package com.example.quotation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.quotation.entity.Category;
import com.example.quotation.mapper.CategoryMapper;
import com.example.quotation.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {


    @Override
    public List<Category> listWithTreeStructure() {
        // 1. 查出所有分类，按排序字段和ID排序
        List<Category> allCategories = this.list(new QueryWrapper<Category>()
                .orderByAsc("sort_order")
                .orderByAsc("id"));

        // 2. 找出所有根分类 (parentId 为 null 或 0)
        List<Category> rootCategories = allCategories.stream()
                .filter(c->c.getParentId() == null || c.getParentId() == 0)
                .collect(Collectors.toList());

        // 3. 递归设置子分类，构建树形结构
        rootCategories.forEach(root->{
            root.setChildren(getChildren(root, allCategories));
        });

        return rootCategories;
    }

    @Override
    public boolean hasChildren(Integer id) {
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getParentId, id);
        if(this.count(queryWrapper) > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean hasProducts(Integer id) {
        // todo 查询当前分类下的所有产品
        return false;
    }

    /**
     * 递归查找当前分类的所有子分类
     */
    private List<Category> getChildren(Category root, List<Category> all) {
        List<Category> children = all.stream()
                .filter(c->root.getId().equals(c.getParentId()))
                .collect(Collectors.toList());

        // 递归设置子分类的子分类
        children.forEach(child->{
            child.setChildren(getChildren(child, all));
        });

        // 如果子分类列表为空，则返回null或空列表，取决于前端需求，这里返回列表
        return children;
    }
}