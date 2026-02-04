package com.example.bookshelf.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 转盘表实体类
 */
@Data
public class GameWheel {
    /**
     * 转盘ID
     */
    private Long id;
    /**
     * 转盘名称
     */
    private String name;
    /**
     * 转盘说明
     */
    private String description;
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    /**
     * 修改时间
     */
    private LocalDateTime updatedAt;
}
