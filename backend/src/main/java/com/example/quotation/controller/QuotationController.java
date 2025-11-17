package com.example.quotation.controller;

import com.example.quotation.common.ApiResponse;
import com.example.quotation.entity.Quotation;
import com.example.quotation.entity.User;
import com.example.quotation.service.QuotationService;
import com.example.quotation.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 报价单控制器
 * 提供报价单的CRUD接口、状态流转接口等
 *
 * @author System
 */
@RestController
@RequestMapping("/api/quotations")
public class QuotationController {

    @Resource
    private QuotationService quotationService;

    @Resource
    private UserService userService;

    /**
     * 获取报价单列表
     * 支持按状态、客户ID筛选，并根据用户角色控制可见范围
     */
    @GetMapping
    public ApiResponse<List<Quotation>> list(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long customerId,
            HttpServletRequest request) {

        // 获取登录用户
        Integer userId = (Integer) request.getAttribute("userId");
        if(userId == null) {
            return ApiResponse.error("UserId missing in request");
        }

        User user = userService.findById(Long.valueOf(userId));
        if(user == null) {
            return ApiResponse.error("User not found");
        }

        boolean isAdmin = "ADMIN".equals(user.getRole());

        // 管理员可以查看全部；普通用户只能查看自己的
        List<Quotation> list;
        if(isAdmin) {
            list = quotationService.listAll();
        } else {
            list = quotationService.listByUserId(user.getId());
        }

        // 条件过滤（可叠加筛选）
        if(status != null && !status.isEmpty()) {
            list = list.stream()
                    .filter(q->status.equals(q.getStatus()))
                    .collect(Collectors.toList());
        }

        if(customerId != null) {
            list = list.stream()
                    .filter(q->customerId.equals(q.getCustomerId()))
                    .collect(Collectors.toList());
        }

        return ApiResponse.ok(list);
    }


    /**
     * 获取报价单详情
     * 包含报价单基本信息和明细项
     *
     * @param id 报价单ID
     * @return 报价单详情
     */
    @GetMapping("/{id}")
    public ApiResponse<Map<String, Object>> get(@PathVariable Long id) {
        Quotation quotation = quotationService.findById(id);
        if(quotation == null) {
            return ApiResponse.error("Quotation not found");
        }
        Map<String, Object> data = new HashMap<>();
        data.put("quotation", quotation);
        data.put("items", quotation.getItems());
        return ApiResponse.ok(data);
    }

    /**
     * 保存报价单（新增或更新）
     *
     * @param quotation 报价单对象
     * @return 保存后的报价单对象
     */
    @PostMapping
    public ApiResponse<Quotation> save(@RequestBody Quotation quotation, HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        if(userId == null) {
            return ApiResponse.error("Not logged in");
        }
        quotation.setCreatedBy(Long.valueOf(userId));
        quotation = quotationService.save(quotation);
        return ApiResponse.ok(quotation);
    }

    /**
     * 从套餐创建报价单
     *
     * @param body    包含packageId、customerId、userId的请求体
     * @param request HTTP请求对象，用于获取当前登录用户信息
     * @return 创建的报价单对象
     */
    @PostMapping("/from-package")
    public ApiResponse<Quotation> createFromPackage(@RequestBody Map<String, Long> body, HttpServletRequest request) {
        Long packageId = body.get("packageId");
        Long customerId = body.get("customerId");
        // 优先使用请求体中的userId，如果没有则从token中获取
        Long userId = body.get("userId");

        if(userId == null) {
            // 从token中获取用户名，再查询userId
            String username = (String) request.getAttribute("username");
            if(username != null) {
                User user = userService.findByUsername(username);
                if(user != null) {
                    userId = user.getId();
                }
            }
        }

        if(packageId == null || customerId == null || userId == null) {
            return ApiResponse.error("Incomplete parameters");
        }

        try {
            Quotation quotation = quotationService.createFromPackage(packageId, customerId, userId);
            return ApiResponse.ok(quotation);
        } catch(Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 提交报价单审批
     * 状态流转：DRAFT -> SUBMITTED
     *
     * @param id      报价单ID
     * @param request HTTP请求对象，用于获取当前登录用户信息
     * @return 更新后的报价单对象
     */
    @PostMapping("/{id}/submit")
    public ApiResponse<Quotation> submit(@PathVariable Long id, HttpServletRequest request) {
        // 从token中获取用户名，再查询userId
        String username = (String) request.getAttribute("username");
        if(username == null) {
            return ApiResponse.error("Not logged in");
        }
        User user = userService.findByUsername(username);
        if(user == null) {
            return ApiResponse.error("User not found");
        }

        try {
            Quotation quotation = quotationService.submit(id, user.getId());
            return ApiResponse.ok(quotation);
        } catch(Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 审批通过报价单
     * 状态流转：SUBMITTED -> APPROVED
     *
     * @param id      报价单ID
     * @param body    包含comment的请求体
     * @param request HTTP请求对象，用于获取当前登录用户信息
     * @return 更新后的报价单对象
     */
    @PostMapping("/{id}/approve")
    public ApiResponse<Quotation> approve(@PathVariable Long id, @RequestBody Map<String, String> body, HttpServletRequest request) {
        String comment = body.get("comment");

        // 从token中获取用户名，再查询userId
        String username = (String) request.getAttribute("username");
        if(username == null) {

            return ApiResponse.error("Not logged in");
        }
        User user = userService.findByUsername(username);
        if(user == null) {
            return ApiResponse.error("User not found");
        }

        try {
            Quotation quotation = quotationService.approve(id, user.getId(), comment);
            return ApiResponse.ok(quotation);
        } catch(Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 拒绝报价单
     * 状态流转：SUBMITTED -> REJECTED
     *
     * @param id      报价单ID
     * @param body    包含comment的请求体
     * @param request HTTP请求对象，用于获取当前登录用户信息
     * @return 更新后的报价单对象
     */
    @PostMapping("/{id}/reject")
    public ApiResponse<Quotation> reject(@PathVariable Long id, @RequestBody Map<String, String> body, HttpServletRequest request) {
        String comment = body.get("comment");

        // 从token中获取用户名，再查询userId
        String username = (String) request.getAttribute("username");
        if(username == null) {
            return ApiResponse.error("Not logged in");
        }
        User user = userService.findByUsername(username);
        if(user == null) {
            return ApiResponse.error("User not found");
        }

        try {
            Quotation quotation = quotationService.reject(id, user.getId(), comment);
            return ApiResponse.ok(quotation);
        } catch(Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 删除报价单
     * 注意：会级联删除报价明细项
     *
     * @param id 报价单ID
     * @return 成功响应
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        quotationService.delete(id);
        return ApiResponse.ok(null);
    }
}
