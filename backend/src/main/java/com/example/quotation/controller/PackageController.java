package com.example.quotation.controller;

import com.example.quotation.common.ApiResponse;
import com.example.quotation.entity.PackageEntity;
import com.example.quotation.entity.PackageItem;
import com.example.quotation.service.PackageItemService;
import com.example.quotation.service.PackageService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/packages")
public class PackageController {

    @Resource
    private PackageService packageService;

    @Resource
    private PackageItemService packageItemService;

    @GetMapping
    public ApiResponse<List<PackageEntity>> list(@RequestParam(required = false) Boolean enabled) {
        if(enabled != null && enabled) {
            return ApiResponse.ok(packageService.listEnabled());
        }
        return ApiResponse.ok(packageService.listAll());
    }

    /**
     * 获取套餐详情（包含产品列表）
     *
     * @param id 套餐ID
     * @return 套餐详情（包含items列表）
     */
    @GetMapping("/{id}")
    public ApiResponse<PackageEntity> get(@PathVariable Long id) {
        PackageEntity pkg = packageService.findById(id);
        if(pkg == null) {
            return ApiResponse.error("Package not found");
        }
        // findById已经包含了items列表
        return ApiResponse.ok(pkg);
    }

    /**
     * 保存套餐（新增或更新）
     * 支持同时保存套餐的产品列表
     *
     * @param pkg 套餐对象（可包含items列表）
     * @return 保存后的套餐对象（包含items列表）
     */
    @PostMapping
    public ApiResponse<PackageEntity> save(@RequestBody PackageEntity pkg) {
        // save方法会自动保存items列表
        pkg = packageService.save(pkg);
        return ApiResponse.ok(pkg);
    }

    /**
     * 更新套餐（包含产品列表）
     *
     * @param id  套餐ID
     * @param pkg 套餐对象（可包含items列表）
     * @return 更新后的套餐对象（包含items列表）
     */
    @PutMapping("/{id}")
    public ApiResponse<PackageEntity> update(@PathVariable Long id, @RequestBody PackageEntity pkg) {
        pkg.setId(id);
        // save方法会自动保存items列表
        pkg = packageService.save(pkg);
        return ApiResponse.ok(pkg);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        packageService.delete(id);
        return ApiResponse.ok(null);
    }

    @GetMapping("/{id}/items")
    public ApiResponse<List<PackageItem>> getItems(@PathVariable Long id) {
        return ApiResponse.ok(packageItemService.listByPackageId(id));
    }

    @PostMapping("/{id}/items")
    public ApiResponse<PackageItem> addItem(@PathVariable Long id, @RequestBody PackageItem item) {
        item.setPackageId(id);
        item = packageItemService.save(item);
        return ApiResponse.ok(item);
    }

    @PutMapping("/{id}/items/{itemId}")
    public ApiResponse<PackageItem> updateItem(@PathVariable Long id, @PathVariable Long itemId, @RequestBody PackageItem item) {
        item.setId(itemId);
        item.setPackageId(id);
        item = packageItemService.save(item);
        return ApiResponse.ok(item);
    }

    @DeleteMapping("/{id}/items/{itemId}")
    public ApiResponse<Void> deleteItem(@PathVariable Long id, @PathVariable Long itemId) {
        packageItemService.delete(itemId);
        return ApiResponse.ok(null);
    }
}
