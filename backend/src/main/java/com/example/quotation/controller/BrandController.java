package com.example.quotation.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.quotation.common.ApiResponse;
import com.example.quotation.entity.Brand;
import com.example.quotation.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 品牌管理 API
 */
@RestController
@RequestMapping("/api/brands")
public class BrandController {

    @Autowired
    private BrandService brandService;

    @PostMapping // POST /brands
    public ApiResponse<Boolean> save(@RequestBody Brand brand) {
        if(StringUtils.isEmpty(brand.getName())) {
            return ApiResponse.error("Brand Name must be null");
        }
        brand.setId(null);
        return ApiResponse.ok(brandService.save(brand));
    }

    @PutMapping // PUT /brands
    public ApiResponse<Boolean> update(@RequestBody Brand brand) {
        if(brand.getId() == null) {
            return ApiResponse.error("Brand ID must be not null");
        }
        if(StringUtils.isEmpty(brand.getName())) {
            return ApiResponse.error("Brand Name must be not null");
        }
        return ApiResponse.ok(brandService.updateById(brand));
    }

    @DeleteMapping("/{id}") // DELETE /brands/{id}
    public ApiResponse<Boolean> delete(@PathVariable Integer id) {
        return ApiResponse.ok(brandService.removeById(id));
    }

    @GetMapping("/{id}") // GET /brands/{id}
    public ApiResponse<Brand> getById(@PathVariable Integer id) {
        return ApiResponse.ok(brandService.getById(id));
    }

    @GetMapping // GET /brands?name=xxx (查询列表)
    public ApiResponse<List<Brand>> list(@RequestParam(required = false) String name) {
        QueryWrapper<Brand> wrapper = new QueryWrapper<>();
        if(name != null && !name.isEmpty()) {
            wrapper.like("name", name);
        }
        wrapper.orderByAsc("name");
        return ApiResponse.ok(brandService.list(wrapper));
    }
}