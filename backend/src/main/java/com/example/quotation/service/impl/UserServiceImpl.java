package com.example.quotation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.quotation.entity.User;
import com.example.quotation.mapper.UserMapper;
import com.example.quotation.service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户服务实现类
 * 负责用户的CRUD操作、密码加密、登录验证等
 * <p>
 * 主要功能：
 * 1. 用户查询（按用户名、ID）
 * 2. 用户保存（新增或更新，自动处理密码加密）
 * 3. 用户注册（检查用户名重复，密码加密）
 *
 * @author System
 */
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    /**
     * BCrypt密码编码器，用于密码加密和验证
     */
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public User findByUsername(String username) {
        QueryWrapper<User> qw = new QueryWrapper<>();
        qw.eq("username", username);
        return userMapper.selectOne(qw);
    }

    @Override
    public User findById(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public List<User> listAll() {
        return userMapper.selectList(null);
    }

    /**
     * 保存用户（新增或更新）
     * 逻辑：
     * 1. 新增时：密码加密、默认启用
     * 2. 更新时：如果密码已加密（以$2a$开头）或为空，保持原密码；否则加密新密码
     *
     * @param user 用户对象
     * @return 保存后的用户对象
     */
    @Override
    public User save(User user) {
        if(user.getId() == null) {
            // 新增用户
            user.setCreatedAt(LocalDateTime.now());
            // 如果密码未加密（不以$2a$开头），则加密
            if(user.getPassword() != null && !user.getPassword().startsWith("$2a$")) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            }
            // 默认启用
            if(user.getEnabled() == null) {
                user.setEnabled(true);
            }
            userMapper.insert(user);
        } else {
            // 更新用户
            user.setUpdatedAt(LocalDateTime.now());
            User existing = userMapper.selectById(user.getId());
            if(existing != null) {
                // 密码处理逻辑：
                // 1. 如果密码为空，保持原密码
                // 2. 如果密码等于原密码，保持原密码
                // 3. 如果密码已加密（以$2a$开头），保持原密码
                // 4. 否则，加密新密码
                if(StringUtils.isEmpty(user.getPassword()) ||
                        user.getPassword().equals(existing.getPassword()) ||
                        user.getPassword().startsWith("$2a$")) {
                    user.setPassword(existing.getPassword());
                } else {
                    // 加密新密码
                    user.setPassword(passwordEncoder.encode(user.getPassword()));
                }
            }
            userMapper.updateById(user);
        }
        return user;
    }

    /**
     * 用户注册
     * 逻辑：
     * 1. 检查用户名是否已存在
     * 2. 密码使用BCrypt加密
     * 3. 默认启用，默认角色为SALES
     *
     * @param user 用户对象
     * @return 注册后的用户对象
     * @throws RuntimeException 如果用户名已存在
     */
    @Override
    public User register(User user) {
        // 检查用户名是否已存在
        if(findByUsername(user.getUsername()) != null) {
            throw new RuntimeException("Username already exists");
        }
        user.setCreatedAt(LocalDateTime.now());
        user.setEnabled(true);
        // 默认角色为SALES（销售）
        if(user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("SALES");
        }
        // 密码加密
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userMapper.insert(user);
        return user;
    }

    @Override
    public User update(User user) {
        return save(user);
    }

    @Override
    public void delete(Long id) {
        userMapper.deleteById(id);
    }
}

