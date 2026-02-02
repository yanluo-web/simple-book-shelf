package com.example.bookshelf.mapper;

import com.example.bookshelf.entity.User;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用户 Mapper 接口，用于数据库操作
 */
@Repository
public interface UserMapper {

    /**
     * 根据用户名查询用户信息（适配表名 sys_user）
     */
    @Select("SELECT id, username, password, nickname, role, enabled FROM `sys_user` WHERE username = #{username} AND enabled=true")
    User selectUserByUsername(String username);

    /**
     * 根据用户名查询用户ID（适配表名 sys_user）
     */
    @Select("SELECT id FROM `sys_user` WHERE username = #{username} AND enabled=true")
    Long selectUserIdByUsername(String username);

    /**
     * 查询所有用户
     */
    @Select("SELECT * FROM sys_user ORDER BY create_time DESC")
    List<User> getAllUsers();

    /**
     * 根据ID查询用户
     */
    @Select("SELECT * FROM sys_user WHERE id = #{id}")
    User getUserById(Long id);

    /**
     * 新增用户
     */
    @Insert("INSERT INTO sys_user (username, password, nickname, role, enabled, create_time) " +
            "VALUES (#{username}, #{password}, #{nickname}, #{role}, #{enabled}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int addUser(User user);

    /**
     * 修改用户（不含密码）
     */
    @Update("UPDATE sys_user SET nickname = #{nickname}, role = #{role}, enabled = #{enabled} " +
            "WHERE id = #{id}")
    int updateUser(User user);

    /**
     * 修改用户密码
     */
    @Update("UPDATE sys_user SET password = #{password} WHERE id = #{id}")
    int updateUserPassword(@Param("id") Long id, @Param("password") String password);

    /**
     * 删除用户
     */
    @Delete("DELETE FROM sys_user WHERE id = #{id}")
    int deleteUser(Long id);
}