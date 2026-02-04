package com.example.bookshelf.service.impl;

import com.example.bookshelf.entity.Book;
import com.example.bookshelf.entity.Shelf;
import com.example.bookshelf.mapper.BookMapper;
import com.example.bookshelf.mapper.ShelfMapper;
import com.example.bookshelf.service.ShelfService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 书架业务实现类
 */
@Service
public class ShelfServiceImpl implements ShelfService {
    @Resource
    private BookMapper bookMapper;
    @Resource
    private ShelfMapper shelfMapper;

    @Override
    public Integer getBookCountByShelfId(Long shelfId,Long uploadUserId) {
        // 避免返回 null，默认返回 0
        Integer count = bookMapper.getBookCountByShelfId(shelfId,uploadUserId);
        return count == null ? 0 : count;
    }

    @Override
    public List<Book> getBookListByShelfId(Long shelfId,Long uploadUserId) {
        return bookMapper.getBookListByShelfId(shelfId,uploadUserId);
    }

    @Override
    public List<Shelf> getAllShelves() {
        return shelfMapper.selectAll();
    }

    @Override
    public Shelf getShelfById(Long shelfId) {
        return shelfMapper.selectById(shelfId);
    }

    @Override
    public void addShelf(String shelfName, String description, Long userId) {
        Shelf shelf = new Shelf();
        shelf.setShelfName(shelfName);
        shelf.setDescription(description);
        shelf.setCreateUserId(userId);
        shelfMapper.insert(shelf);
    }

    @Override
    public void updateShelf(Long shelfId, String shelfName, String description) {
        Shelf shelf = new Shelf();
        shelf.setId(shelfId);
        shelf.setShelfName(shelfName);
        shelf.setDescription(description);
        shelfMapper.updateById(shelf);
    }

    @Override
    public void deleteShelf(Long shelfId) {
        // 1. 禁止删除默认书架（防止误删）
        if (shelfId == 1) {
            throw new RuntimeException("默认书架不能删除！");
        }

        // 2. 将该书架下的所有书籍 转移到 默认书架（ID=1）
        shelfMapper.moveBooksToDefaultShelf(shelfId, 1L);

        // 3. 再删除书架本身
        shelfMapper.deleteById(shelfId);
    }

    @Override
    public List<Shelf> getShelvesByUserId(Long userId) {
        return shelfMapper.selectByUserId(userId);
    }
}
