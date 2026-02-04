package com.example.bookshelf.mapper;

import com.example.bookshelf.entity.GameWheel;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface GameWheelMapper {
    @Select("SELECT * FROM game_wheel")
    List<GameWheel> selectAll();

    @Select("SELECT * FROM game_wheel WHERE id = #{id}")
    GameWheel selectById(Long id);
}
