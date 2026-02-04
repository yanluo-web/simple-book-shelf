package com.example.bookshelf.mapper;

import com.example.bookshelf.entity.Book;
import com.example.bookshelf.entity.ReadRecord;
import com.example.bookshelf.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

/**
 * 阅读记录数据访问层（操作 read_record 表，与 ReadRecord PO 字段对应）
 * @Mapper 注解：让 MyBatis 扫描并生成代理实现类，无需手动编写实现
 */
@Mapper
public interface ReadRecordMapper {
    /**
     * 根据用户和书籍查询最新阅读记录（一个用户对一本书只有一条记录）
     * @return 封装为 Optional，避免空指针异常
     */
    @Select("SELECT * FROM read_record WHERE user_id = #{user.id} AND book_id = #{book.id}")
    Optional<ReadRecord> findByUserAndBook(@Param("user") User user, @Param("book") Book book);

    /**
     * 根据用户查询所有阅读记录（按最后阅读时间降序排序）
     */
    @Select("SELECT * FROM read_record WHERE user_id = #{user.id} ORDER BY last_read_time DESC")
    List<ReadRecord> findByUserOrderByLastReadTimeDesc(@Param("user") User user);

    /**
     * 插入新的阅读记录
     * @param readRecord 阅读记录实体
     * @return 受影响的行数
     */
    @Insert("INSERT INTO read_record (user_id, book_id, chapter_id, read_position, last_read_time) " +
            "VALUES (#{userId}, #{bookId}, #{chapterId}, #{readPosition}, NOW())")
    int save(ReadRecord readRecord);

    /**
     * 更新阅读记录（更新阅读位置和最后阅读时间）
     * @param readRecord 携带更新数据的实体
     * @return 受影响的行数
     */
    @Update("UPDATE read_record SET read_position = #{readPosition}, chapter_id = #{chapterId}, last_read_time = NOW() " +
            "WHERE user_id = #{userId} AND book_id = #{bookId}")
    int updateByUserBookChapter(ReadRecord readRecord);

    /**
     * 根据书籍ID删除对应的所有阅读记录
     * 用于删除书籍时，级联删除该书籍的所有阅读记录
     * @param bookId 书籍ID
     * @return 受影响的行数
     */
    @Delete("DELETE FROM read_record WHERE book_id = #{bookId}")
    int deleteByBookId(@Param("bookId") Long bookId);
}