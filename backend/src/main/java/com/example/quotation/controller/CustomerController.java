package com.example.quotation.controller;

import com.example.quotation.common.ApiResponse;
import com.example.quotation.entity.Customer;
import com.example.quotation.entity.Quotation;
import com.example.quotation.service.CustomerService;
import com.example.quotation.service.QuotationService;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Resource
    private CustomerService customerService;

    @Resource
    private QuotationService quotationService;

    @GetMapping
    public ApiResponse<List<Customer>> list() {
        return ApiResponse.ok(customerService.listAll());
    }

    @GetMapping("/{id}")
    public ApiResponse<Customer> get(@PathVariable Long id) {
        Customer customer = customerService.findById(id);
        if(customer == null) {
            return ApiResponse.error("Customer not found");
        }
        return ApiResponse.ok(customer);
    }

    @PostMapping
    public ApiResponse<Customer> save(@RequestBody Customer customer) {
        customer = customerService.save(customer);
        return ApiResponse.ok(customer);
    }

    @PutMapping("/{id}")
    public ApiResponse<Customer> update(@PathVariable Long id, @RequestBody Customer customer) {
        customer.setId(id);
        customer = customerService.save(customer);
        return ApiResponse.ok(customer);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        Customer customer = customerService.findById(id);
        if(customer == null) {
            return ApiResponse.error("Customer not found");
        }
        List<Quotation> quotations = quotationService.listByCustomerId(id);
        if(!CollectionUtils.isEmpty(quotations)) {
            return ApiResponse.error("Cannot delete customer with existing quotations");
        }
        customerService.delete(id);
        return ApiResponse.ok(null);
    }
}

