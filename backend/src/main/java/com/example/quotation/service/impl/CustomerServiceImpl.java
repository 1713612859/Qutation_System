package com.example.quotation.service.impl;

import com.example.quotation.entity.Customer;
import com.example.quotation.mapper.CustomerMapper;
import com.example.quotation.service.CustomerService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Resource
    private CustomerMapper customerMapper;

    @Override
    public List<Customer> listAll() {
        return customerMapper.selectList(null);
    }

    @Override
    public Customer findById(Long id) {
        return customerMapper.selectById(id);
    }

    @Override
    public Customer save(Customer customer) {
        if(customer.getId() == null) {
            customer.setCreatedAt(LocalDateTime.now());
            customerMapper.insert(customer);
        } else {
            customer.setUpdatedAt(LocalDateTime.now());
            customerMapper.updateById(customer);
        }
        return customer;
    }

    @Override
    public void delete(Long id) {
        customerMapper.deleteById(id);
    }
}

