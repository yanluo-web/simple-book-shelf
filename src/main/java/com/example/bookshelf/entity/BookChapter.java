package com.example.bookshelf.entity;

import lombok.Data;

/**
 * 书籍章节实体（对应 H2 数据库表：book_chapter）
 */
@Data
public class BookChapter {
    /**
     * 章节主键
     */
    private Long id;

    /**
     * 章节标题（对应数据库字段：chapter_title）
     */
    private String chapterTitle;

    /**
     * 章节内容（对应数据库字段：chapter_content）
     */
    private String chapterContent;

    /**
     * 章节序号（对应数据库字段：chapter_order）
     */
    private Integer chapterOrder;

    /**
     * 关联所属书籍的 ID（对应数据库外键字段：book_id，用于数据库映射和批量插入）
     */
    private Long bookId;

    /**
     * 关联所属书籍（对象类型，用于业务层关联查询，非数据库直接映射字段）
     */
    private Book book;
}