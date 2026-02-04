package com.example.bookshelf.service;

import com.example.bookshelf.entity.Book;

import java.util.List;

/**
 * 书籍业务逻辑层接口
 * 基于现有 Book PO 封装业务方法，职责：处理业务逻辑、入参校验、调用 Mapper 操作数据库
 */
public interface BookService {

    /**
     * 保存新书籍（封装书籍入库的核心业务逻辑）
     *
     * @param bookName     书籍名称
     * @param author       作者
     * @param coverPath    书籍封面路径（可为 null，无封面时传空字符串）
     * @param filePath     书籍文件本地存储路径（必填）
     * @param format       书籍格式（TXT/EPUB，必填）
     * @param shelfId      关联书架ID（必填）
     * @param uploadUserId 上传人ID（必填，关联用户表）
     */
    void saveBook(String bookName,
                  String author,
                  String coverPath,
                  String filePath,
                  String format,
                  Long shelfId,
                  Long uploadUserId);

    /**
     * 1. 根据上传人ID查询我的书籍列表
     * @param uploadUserId
     * @return
     */
    List<Book> getMyBookList(Long uploadUserId);

    /**
     * 根据书籍ID查询书籍信息
     * @param bookId
     * @return
     */
    Book getBookById(Long bookId);

    /**
     * 根据书籍ID删除书籍
     * @param bookId
     */
    void deleteBookById(Long bookId);
}