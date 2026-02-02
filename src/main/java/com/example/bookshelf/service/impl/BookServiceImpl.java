package com.example.bookshelf.service.impl;

import com.example.bookshelf.entity.Book;
import com.example.bookshelf.mapper.BookMapper;
import com.example.bookshelf.service.BookService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * 书籍业务逻辑层实现类
 * @Service 注解：将该类注册为 Spring 容器 Bean，供 Controller 注入使用
 */
@Service
public class BookServiceImpl implements BookService {

    /**
     * 注入 BookMapper，用于操作数据库（数据访问层）
     */
    @Resource
    private BookMapper bookMapper;

    /**
     * 保存新书籍（具体业务逻辑实现，匹配现有 Book PO 字段）
     */
    @Override
    public void saveBook(String bookName,
                         String author,
                         String coverPath,
                         String filePath,
                         String format,
                         Long shelfId,
                         Long uploadUserId) {
        // 1. 入参校验（业务层核心：避免无效/非法数据入库，减少后续问题）
        if (bookName == null || bookName.trim().isEmpty()) {
            throw new IllegalArgumentException("书籍名称不能为空");
        }
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("书籍文件路径不能为空");
        }
        if (format == null || (!"TXT".equals(format.toUpperCase()) && !"EPUB".equals(format.toUpperCase()))) {
            throw new IllegalArgumentException("书籍格式仅支持 TXT 和 EPUB");
        }
        if (shelfId == null || shelfId <= 0) {
            throw new IllegalArgumentException("关联书架ID不合法");
        }
        if (uploadUserId == null || uploadUserId <= 0) {
            throw new IllegalArgumentException("上传人ID不合法");
        }

        // 2. 封装 Book PO 实体（匹配现有字段，填充业务数据和默认值）
        Book book = new Book();
        book.setBookName(bookName.trim());
        book.setAuthor(author == null ? "" : author.trim()); // 作者可为空，默认填充空字符串
        book.setCoverPath(coverPath == null ? "" : coverPath.trim()); // 封面路径可为空，默认填充空字符串
        book.setFilePath(filePath.trim());
        book.setFormat(format.toUpperCase()); // 统一转为大写，保证数据库格式一致
        book.setShelfId(shelfId);
        book.setUploadUserId(uploadUserId);
        book.setUploadTime(new Date()); // 填充当前时间作为上传时间

        // 3. 调用 Mapper 插入数据库（将 PO 数据持久化到数据库）
        int affectedRows = bookMapper.insertBook(book);

        // 4. 校验入库结果（增强业务健壮性，避免静默入库失败）
        if (affectedRows != 1) {
            throw new RuntimeException("书籍入库失败：未插入任何数据到数据库");
        }
    }

    @Override
    public List<Book> getMyBookList(Long uploadUserId) {
        return bookMapper.getMyBookList(uploadUserId);
    }

    @Override
    public Book getBookById(Long bookId) {
        return bookMapper.getBookById(bookId);
    }

    @Override
    public void deleteBookById(Long bookId) {
        bookMapper.deleteBookById(bookId);
    }
}