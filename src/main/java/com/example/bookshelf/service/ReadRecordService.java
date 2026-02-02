package com.example.bookshelf.service;

import com.example.bookshelf.entity.Book;
import com.example.bookshelf.entity.BookChapter;
import com.example.bookshelf.entity.ReadRecord;
import com.example.bookshelf.entity.User;
import com.example.bookshelf.mapper.ReadRecordMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 阅读记录业务服务
 */
public interface ReadRecordService {
    /**
     * 更新/保存阅读记录（存在则更新，不存在则新增）
     */
    void saveOrUpdateReadRecord(User user, Book book, BookChapter chapter, Integer readPosition);

    /**
     * 根据用户和书籍查询阅读记录
     */
    Optional<ReadRecord> getReadRecordByUserAndBook(User user, Book book);

    /**
     * 根据用户查询所有阅读记录
     */
    List<ReadRecord> getReadRecordsByUser(User user);
}

