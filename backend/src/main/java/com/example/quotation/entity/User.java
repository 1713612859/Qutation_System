
package com.example.quotation.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体类
 * 对应数据库表 users
 */
@TableName("users")
@Data
public class User {
    /**
     * 用户ID（主键）
     */
    @TableId
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 用户全名
     */
    private String fullName;

    /**
     * 电子邮箱
     */
    private String email;

    /**
     * 用户角色
     * 可选值：ADMIN（管理员）, SALES（销售）, APPROVER（审批人）, VIEWER（查看者）
     */
    private String role;

    /**
     * 账号是否启用
     */
    private Boolean enabled;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
