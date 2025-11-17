package com.example.quotation.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@TableName("users")
@Data
public class User {
    @TableId
    private Long id;
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String role; // ADMIN, SALES, APPROVER, VIEWER
    private Boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
