package com.example.bookshelf.service.impl;

import com.example.bookshelf.entity.Book;
import com.example.bookshelf.entity.BookChapter;
import com.example.bookshelf.entity.ReadRecord;
import com.example.bookshelf.entity.User;
import com.example.bookshelf.mapper.ReadRecordMapper;
import com.example.bookshelf.service.ReadRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 阅读记录业务服务
 */
@Service
@RequiredArgsConstructor
public class ReadRecordServiceImpl implements ReadRecordService {
    private final ReadRecordMapper readRecordMapper;

    /**
     * 更新/保存阅读记录（存在则更新，不存在则新增）
     */
    @Transactional
    public void saveOrUpdateReadRecord(User user, Book book, BookChapter chapter, Integer readPosition) {
        // 1. 查询用户是否已有该书籍的阅读记录
        Optional<ReadRecord> optionalRecord = readRecordMapper.findByUserAndBook(user, book);
        ReadRecord readRecord;
        if (optionalRecord.isPresent()) {
            // 2. 存在则更新记录
            readRecord = optionalRecord.get();
            readRecord.setChapterId(chapter.getId());
            readRecord.setReadPosition(readPosition);
            readRecordMapper.updateByUserBookChapter(readRecord);
        } else {
            // 3. 不存在则新增记录
            readRecord = new ReadRecord();
            readRecord.setUserId(user.getId());
            readRecord.setBookId(book.getId());
            readRecord.setChapterId(chapter.getId());
            readRecord.setReadPosition(readPosition == null ? 0 : readPosition);
            readRecordMapper.save(readRecord);
        }
    }

    /**
     * 根据用户和书籍查询阅读记录
     */
    public Optional<ReadRecord> getReadRecordByUserAndBook(User user, Book book) {
        return readRecordMapper.findByUserAndBook(user, book);
    }

    /**
     * 根据用户查询所有阅读记录
     */
    public List<ReadRecord> getReadRecordsByUser(User user) {
        return readRecordMapper.findByUserOrderByLastReadTimeDesc(user);
    }

    @Override
    public void deleteReadRecordsByBookId(Long bookId) {
        // 调用Mapper层方法删除数据
        readRecordMapper.deleteByBookId(bookId);
    }
}
