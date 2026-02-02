package com.example.bookshelf.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BCryptTest {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        // 生成明文 "123456" 对应的 BCrypt 加密串
        String encryptedPassword = encoder.encode("123456");
        System.out.println(encryptedPassword); // 输出类似：$2a$10$xxxxxx...（60位左右）
    }
}
