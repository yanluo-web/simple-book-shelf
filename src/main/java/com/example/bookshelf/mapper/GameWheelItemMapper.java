package com.example.bookshelf.mapper;

import com.example.bookshelf.entity.GameWheelItem;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface GameWheelItemMapper {
    @Select("SELECT * FROM game_wheel_item WHERE wheel_id = #{wheelId}")
    List<GameWheelItem> selectByWheelId(Long wheelId);
}
