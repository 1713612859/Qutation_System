package com.example.quotation.service;

import com.example.quotation.entity.User;

import java.util.List;

public interface UserService {
    User findByUsername(String username);

    User findById(Long id);

    List<User> listAll();

    User save(User user);

    User register(User user);

    User update(User user);

    void delete(Long id);
}

