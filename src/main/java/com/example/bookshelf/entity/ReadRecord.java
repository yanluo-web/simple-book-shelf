package com.example.bookshelf.entity;

import lombok.Data;

import java.util.Date;

/**
 * 阅读记录实体（对应 H2 数据库表：read_record）
 */
@Data
public class ReadRecord {
    /**
     * 记录主键
     */
    private Long id;

    /**
     * 关联用户（多对一：一个用户有多条阅读记录）
     */
    private Long userId;

    /**
     * 关联书籍（多对一：一本书可被多个用户阅读，一个用户对一本书只有一条最新记录）
     */
    private Long bookId;

    /**
     * 关联最后阅读的章节
     */
    private Long chapterId;
    private BookChapter chapter;

    /**
     * 章节内阅读位置（字符索引，用于恢复到上次阅读的位置）
     */
    private Integer readPosition = 0;

    /**
     * 最后阅读时间
     */
    private Date lastReadTime;

    /**
     * 保存前自动更新最后阅读时间
     */
    public void prePersist() {
        this.lastReadTime = new Date();
    }
}