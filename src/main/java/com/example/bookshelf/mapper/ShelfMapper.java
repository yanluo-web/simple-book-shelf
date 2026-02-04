package com.example.bookshelf.mapper;

import com.example.bookshelf.entity.Shelf;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface ShelfMapper {

    /**
     * 查询所有书架（关联用户表，获取创建人昵称）
     */
    @Select("SELECT s.*, u.nickname AS createUserName " +
            "FROM sys_shelf s " +
            "LEFT JOIN sys_user u ON s.create_user_id = u.id")
    List<Shelf> selectAll();

    @Select("SELECT * FROM sys_shelf WHERE id = #{shelfId}")
    Shelf selectById(@Param("shelfId") Long shelfId);

    @Insert("INSERT INTO sys_shelf (shelf_name, description, create_user_id, create_time) " +
            "VALUES (#{shelfName}, #{description}, #{createUserId}, NOW())")
    int insert(Shelf shelf);

    @Update("UPDATE sys_shelf SET shelf_name = #{shelfName}, description = #{description} WHERE id = #{id}")
    int updateById(Shelf shelf);

    @Delete("DELETE FROM sys_book WHERE shelf_id = #{shelfId}")
    int deleteBooksByShelfId(@Param("shelfId") Long shelfId);

    @Delete("DELETE FROM sys_shelf WHERE id = #{shelfId}")
    int deleteById(@Param("shelfId") Long shelfId);

    /**
     * 根据用户ID查询书架（并统计该用户在该书架中的书籍数量）
     */
    @Select("SELECT s.*, " +
            "(SELECT COUNT(*) FROM sys_book b WHERE b.shelf_id = s.id AND b.upload_user_id = #{userId}) AS bookCount " +
            "FROM sys_shelf s " +
            "WHERE s.create_user_id = #{userId}")
    List<Shelf> selectByUserId(@Param("userId") Long userId);
}