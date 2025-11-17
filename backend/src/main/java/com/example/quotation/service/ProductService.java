package com.example.quotation.service;

import com.example.quotation.entity.Product;

import java.util.List;

public interface ProductService {

    List<Product> listAll();

    List<Product> listEnabled();

    Product findById(Long id);

    Product save(Product p);

    void delete(Long id);
}
