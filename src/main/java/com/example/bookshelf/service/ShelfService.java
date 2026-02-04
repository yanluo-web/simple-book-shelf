package com.example.bookshelf.service;

import com.example.bookshelf.entity.Book;
import com.example.bookshelf.entity.Shelf;

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

    List<Shelf> getAllShelves();
    Shelf getShelfById(Long shelfId);
    void addShelf(String shelfName, String description, Long userId);
    void updateShelf(Long shelfId, String shelfName, String description);
    void deleteShelf(Long shelfId);

    /**
     * 根据用户ID查询该用户的所有书架
     * @param userId 用户ID
     * @return 书架列表
     */
    List<Shelf> getShelvesByUserId(Long userId);
}
