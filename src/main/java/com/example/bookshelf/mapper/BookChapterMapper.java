package com.example.bookshelf.mapper;

import com.example.bookshelf.entity.Book;
import com.example.bookshelf.entity.BookChapter;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 章节数据访问层（操作 book_chapter 表，与 BookChapter PO 字段对应）
 * @Mapper 注解：让 MyBatis 扫描并生成代理实现类，无需手动编写实现
 */
@Mapper
public interface BookChapterMapper {

    /**
     * 根据书籍查询所有章节（按章节序号升序排序）
     */
     @Select("SELECT * FROM book_chapter WHERE book_id = #{bookId} ORDER BY chapter_order ASC")
     List<BookChapter> findByBookIdOrderByChapterOrderAsc(@Param("bookId") Long bookId);

    /**
     * 根据书籍和章节序号查询章节
     */
     @Select("SELECT * FROM book_chapter WHERE book_id = #{bookId} AND chapter_order = #{chapterOrder}")
     BookChapter findByBookIdAndChapterOrder(@Param("bookId") Long bookId, @Param("chapterOrder") Integer chapterOrder);

    /**
     * 批量插入章节（批量保存，效率高于单条循环插入）
     * 调整：现在 #{chapter.bookId} 可正常映射，因为 BookChapter 已新增 bookId 属性
     * @param bookChapters 章节列表
     * @return 受影响的行数
     */
    @Insert("<script>" +
            "INSERT INTO book_chapter (chapter_title, chapter_content, chapter_order, book_id) " +
            "VALUES " +
            "<foreach collection='bookChapters' item='chapter' separator=','>" +
            "(#{chapter.chapterTitle}, #{chapter.chapterContent}, #{chapter.chapterOrder}, #{chapter.bookId})" +
            "</foreach>" +
            "</script>")
    int saveAll(@Param("bookChapters") List<BookChapter> bookChapters);

    /**
     * 根据章节ID查询章节详情
     * @param chapterId 章节ID
     * @return 章节实体
     */
    @Select("SELECT * FROM book_chapter WHERE id = #{chapterId}")
    BookChapter findById(Long chapterId);
}