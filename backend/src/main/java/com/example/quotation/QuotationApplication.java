package com.example.quotation;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(value = {"com.example.quotation.mapper"})
public class QuotationApplication {
    public static void main(String[] args) {
        SpringApplication.run(QuotationApplication.class, args);
    }
}
