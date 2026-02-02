package com.example.bookshelf.service.impl;

import com.example.bookshelf.entity.User;
import com.example.bookshelf.mapper.UserMapper;
import com.example.bookshelf.service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Resource
    private UserMapper userMapper;
    // 密码加密器（Spring Security 提供，需引入依赖）
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 查询所有用户
     */
    public List<User> getAllUsers() {
        return userMapper.getAllUsers();
    }

    /**
     * 新增用户（密码自动加密）
     */
    public boolean addUser(User user) {
        if (!StringUtils.hasText(user.getUsername()) || !StringUtils.hasText(user.getPassword())) {
            return false;
        }
        // 密码加密后存入数据库
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // 默认角色 USER，启用状态 true
        if (!StringUtils.hasText(user.getRole())) {
            user.setRole("USER");
        }
        user.setEnabled(true);
        return userMapper.addUser(user) > 0;
    }

    /**
     * 修改用户（不含密码）
     */
    public boolean updateUser(User user) {
        if (user.getId() == null || !StringUtils.hasText(user.getNickname())) {
            return false;
        }
        return userMapper.updateUser(user) > 0;
    }

    /**
     * 修改用户密码（密码自动加密）
     */
    public boolean updateUserPassword(Long id, String newPassword) {
        if (id == null || !StringUtils.hasText(newPassword)) {
            return false;
        }
        String encodedPassword = passwordEncoder.encode(newPassword);
        return userMapper.updateUserPassword(id, encodedPassword) > 0;
    }

    /**
     * 删除用户（禁止删除管理员 admin，可选）
     */
    public boolean deleteUser(Long id) {
        if (id == null) {
            return false;
        }
        User user = userMapper.getUserById(id);
        if (user != null && "admin".equals(user.getUsername())) {
            return false; // 禁止删除管理员
        }
        return userMapper.deleteUser(id) > 0;
    }

    /**
     * 判断是否为管理员
     */
    public boolean isAdmin(User user) {
        return user != null && "ADMIN".equals(user.getRole());
    }
}
