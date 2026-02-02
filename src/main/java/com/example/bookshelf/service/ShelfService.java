package com.example.bookshelf.service;

import com.example.bookshelf.entity.Book;

import java.util.List;

/**
 * 书架业务接口
 */
public interface ShelfService {
    /**
     * 根据书架ID查询书籍数量
     */
    Integer getBookCountByShelfId(Long shelfId,Long uploadUserId);

    /**
     * 根据书架ID查询书籍列表
     */
    List<Book> getBookListByShelfId(Long shelfId,Long uploadUserId);
}
