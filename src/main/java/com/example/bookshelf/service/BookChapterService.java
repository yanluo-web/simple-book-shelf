package com.example.bookshelf.service;

import com.example.bookshelf.entity.Book;
import com.example.bookshelf.entity.BookChapter;

import java.io.File;
import java.util.List;

public interface BookChapterService {

    /**
     * 拆分 TXT 文件为章节并保存到数据库
     * @param book 所属书籍实体
     * @param txtFile 上传的 TXT 文件
     */
    void splitTxtToChapters(Book book, File txtFile);

    /**
     * 根据书籍查询所有章节
     */
     List<BookChapter> getChaptersByBook(Book book);

    /**
     * 根据书籍和章节序号查询章节
     */
    BookChapter getChapterByBookAndOrder(Book book, Integer chapterOrder);

    /**
     * 新增：根据章节ID查询章节（封装 JpaRepository 的 findById，解决报红）
     * @param chapterId 章节主键ID
     * @return 章节实体
     */
    BookChapter getById(Long chapterId);
}
