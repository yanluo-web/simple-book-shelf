package com.example.bookshelf.mapper;

import com.example.bookshelf.entity.Book;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 书籍数据访问层（操作书籍相关数据库表，与 Book PO 字段对应）
 * @Mapper 注解：让 MyBatis 扫描并生成代理实现类，无需手动编写实现
 */
@Mapper
public interface BookMapper {

    /**
     * 插入新书籍（全字段插入，自增 id 和自动填充的 uploadTime 除外）
     * 注意：SQL 中的字段名（下划线命名）与 PO 中的驼峰字段名自动映射（已配置 map-underscore-to-camel-case: true）
     * @param book 书籍实体（封装待入库的书籍数据）
     * @return 受影响的行数（1 表示插入成功，0 表示插入失败）
     */
    @Insert("INSERT INTO sys_book (" +
            "book_name, author, cover_path, file_path, format, shelf_id, upload_user_id, upload_time" +
            ") VALUES (" +
            "#{bookName}, #{author}, #{coverPath}, #{filePath}, #{format}, #{shelfId}, #{uploadUserId}, #{uploadTime}" +
            ")")
    int insertBook(Book book);

    // 新增：查询所有书籍（用于列表展示）
    @Select("SELECT * FROM sys_book ORDER BY upload_time DESC")
    List<Book> getBookList();

    /**
     * 根据书架ID查询书籍数量（实时统计书架书籍数）
     */
    @Select("SELECT COUNT(*) FROM sys_book WHERE shelf_id = #{shelfId} AND upload_user_id  = #{uploadUserId}")
    Integer getBookCountByShelfId(Long shelfId,Long uploadUserId);

    /**
     * 根据书架ID查询书籍列表（书架详情）
     */
    @Select("SELECT * FROM sys_book WHERE shelf_id = #{shelfId} AND upload_user_id  = #{uploadUserId} ORDER BY upload_time DESC")
    List<Book> getBookListByShelfId(Long shelfId,Long uploadUserId);

    /**
     * 根据书籍ID查询书籍详情（阅读页面用）
     */
    @Select("SELECT * FROM sys_book WHERE id = #{bookId}")
    Book getBookById(Long bookId);

    /**
     * 1. 根据上传人ID查询我的书籍列表
     * @param uploadUserId
     * @return
     */
    @Select("SELECT * FROM sys_book WHERE upload_user_id = #{uploadUserId}")
    List<Book> getMyBookList(Long uploadUserId);

    /**
     * 根据书籍ID删除书籍
     * @param bookId
     */
    @Delete("DELETE FROM sys_book WHERE id = #{bookId}")
    void deleteBookById(Long bookId);
}