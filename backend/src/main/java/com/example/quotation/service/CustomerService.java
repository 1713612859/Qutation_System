package com.example.quotation.service;

import com.example.quotation.entity.Customer;

import java.util.List;

public interface CustomerService {
    List<Customer> listAll();

    Customer findById(Long id);

    Customer save(Customer customer);

    void delete(Long id);
}

