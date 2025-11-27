package com.example.quotation.controller;


import com.example.quotation.common.ApiResponse;
import com.example.quotation.entity.Category;
import com.example.quotation.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类管理 API
 */
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping // POST /categories
    public ApiResponse<Boolean> save(@RequestBody Category category) {
        if(StringUtils.isEmpty(category.getName())) {
            return ApiResponse.error("Category Name must be not null");
        }
        category.setParentId(category.getParentId() == null ? 0 : category.getParentId());
        return ApiResponse.ok(categoryService.save(category));
    }

    @PutMapping // PUT /categories
    public ApiResponse<Boolean> update(@RequestBody Category category) {
        if(category.getId() == null) {
            return ApiResponse.error("Category ID must be not null");
        }
        if(StringUtils.isEmpty(category.getName())) {
            return ApiResponse.error("Category Name must be not null");
        }
        category.setParentId(category.getParentId() == null ? 0 : category.getParentId());
        return ApiResponse.ok(categoryService.updateById(category));
    }

    @DeleteMapping("/{id}") // DELETE /categories/{id}
    public ApiResponse<Boolean> delete(@PathVariable Integer id) {
        // 建议：在实际删除前，业务层应检查该分类下是否有产品或子分类，避免数据完整性破坏。
        if(id == null) {
            return ApiResponse.error("Category ID must be not null");
        }
        if(id == 0) {
            return ApiResponse.error("Cannot delete root category");
        }
        if(categoryService.hasChildren(id)) {
            return ApiResponse.error("Cannot delete category with children");
        }
        if(categoryService.hasProducts(id)) {
            return ApiResponse.error("Cannot delete category with products");
        }

        return ApiResponse.ok(categoryService.removeById(id));
    }

    @GetMapping("/{id}") // GET /categories/{id}
    public ApiResponse<Category> getById(@PathVariable Integer id) {
        return ApiResponse.ok(categoryService.getById(id));
    }

    // GET /categories (获取树形结构列表)
    @GetMapping
    public ApiResponse<List<Category>> listTree() {
        return ApiResponse.ok(categoryService.listWithTreeStructure());
    }
}