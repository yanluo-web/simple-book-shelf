package com.example.bookshelf.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Shelf {
    /**
     * 书架ID
     */
    private Long id;

    /**
     * 书架名称
     */
    private String shelfName;

    /**
     * 书架描述
     */
    private String description;

    /**
     * 创建人ID
     */
    private Long createUserId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建人昵称（新增，用于前端显示）
     */
    private String createUserName;

    /**
     * 书架内书籍数量（用于首页展示）
     */
    private Integer bookCount;
}