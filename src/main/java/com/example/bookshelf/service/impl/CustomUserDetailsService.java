package com.example.bookshelf.service.impl;

import com.example.bookshelf.entity.User;
import com.example.bookshelf.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 自定义用户详情服务，用于 Spring Security 登录验证（从数据库查询用户）
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    // 这里先采用直接注入 Mapper 的方式（后续可补充完整 Service 层）
    @Autowired
    private UserMapper userMapper;

    /**
     * 根据用户名查询用户信息（Spring Security 自动调用该方法进行登录验证）
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. 从数据库中查询用户信息
        User user = userMapper.selectUserByUsername(username);

        // 2. 若用户不存在，抛出异常（Spring Security 会自动处理为登录失败）
        if (user == null) {
            throw new UsernameNotFoundException("用户名不存在：" + username);
        }

        // 3. 封装为 Spring Security 的 UserDetails 对象（包含用户名、密码、角色）
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole()) // 角色（ADMIN/USER）
                .build();
    }
}