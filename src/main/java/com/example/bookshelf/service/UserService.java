package com.example.bookshelf.service;

import com.example.bookshelf.entity.User;

import java.util.List;

public interface UserService {
    /**
     * 查询所有用户
     */
    List<User> getAllUsers();

    /**
     * 新增用户（密码自动加密）
     */
    boolean addUser(User user);

    /**
     * 修改用户（不含密码）
     */
    boolean updateUser(User user);

    /**
     * 修改用户密码（密码自动加密）
     */
    boolean updateUserPassword(Long id, String newPassword);

    /**
     * 删除用户（禁止删除管理员 admin，可选）
     */
    boolean deleteUser(Long id);

    /**
     * 判断是否为管理员
     */
    boolean isAdmin(User user);
}

