package com.example.bookshelf.service;

import com.example.bookshelf.entity.GameWheel;
import com.example.bookshelf.entity.GameWheelItem;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface GameWheelService {
    /**
     * 获取所有转盘
     */
    List<GameWheel> getAllWheels();

    /**
     * 根据ID获取转盘
     */
    GameWheel getWheelById(Long id);

    /**
     * 根据转盘ID获取所有选项
     */
    List<GameWheelItem> getItemsByWheelId(Long wheelId);

    /**
     * 权重随机抽取一个选项
     */
    GameWheelItem randomItem(Long wheelId);
}
