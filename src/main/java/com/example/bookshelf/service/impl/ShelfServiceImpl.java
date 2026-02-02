package com.example.bookshelf.service.impl;

import com.example.bookshelf.entity.Book;
import com.example.bookshelf.mapper.BookMapper;
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
}
