package com.example.quotation.controller;

import com.example.quotation.common.ApiResponse;
import com.example.quotation.entity.Product;
import com.example.quotation.service.ProductService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Resource
    private ProductService productService;

    @GetMapping
    public ApiResponse<List<Product>> list(@RequestParam(required = false) Boolean enabled) {
        if(enabled != null && enabled) {
            return ApiResponse.ok(productService.listEnabled());
        }
        return ApiResponse.ok(productService.listAll());
    }

    @GetMapping("/{id}")
    public ApiResponse<Product> get(@PathVariable Long id) {
        Product product = productService.findById(id);
        if(product == null) {
            return ApiResponse.error("Product not found");
        }
        return ApiResponse.ok(product);
    }

    @PostMapping
    public ApiResponse<Product> save(@RequestBody Product product) {
        product = productService.save(product);
        return ApiResponse.ok(product);
    }

    @PutMapping("/{id}")
    public ApiResponse<Product> update(@PathVariable Long id, @RequestBody Product product) {
        product.setId(id);
        product = productService.save(product);
        return ApiResponse.ok(product);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ApiResponse.ok(null);
    }
}
