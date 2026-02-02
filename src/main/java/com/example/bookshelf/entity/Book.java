package com.example.bookshelf.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Book {
    /**
     * 书籍ID
     */
    private Long id;

    /**
     * 书籍名称
     */
    private String bookName;

    /**
     * 作者
     */
    private String author;

    /**
     * 书籍封面路径
     */
    private String coverPath;

    /**
     * 书籍文件路径（本地存储）
     */
    private String filePath;

    /**
     * 书籍格式（TXT/EPUB）
     */
    private String format;

    /**
     * 关联书架ID
     */
    private Long shelfId;

    /**
     * 上传人ID
     */
    private Long uploadUserId;

    /**
     * 上传时间
     */
    private Date uploadTime;
}