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
}