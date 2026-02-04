package com.example.bookshelf.service.impl;


import com.example.bookshelf.entity.GameWheel;
import com.example.bookshelf.entity.GameWheelItem;
import com.example.bookshelf.mapper.GameWheelItemMapper;
import com.example.bookshelf.mapper.GameWheelMapper;
import com.example.bookshelf.service.GameWheelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class GameWheelServiceImpl implements GameWheelService {

    @Autowired
    private GameWheelMapper gameWheelMapper;

    @Autowired
    private GameWheelItemMapper gameWheelItemMapper;

    @Override
    public List<GameWheel> getAllWheels() {
        return gameWheelMapper.selectAll();
    }

    @Override
    public GameWheel getWheelById(Long id) {
        return gameWheelMapper.selectById(id);
    }

    @Override
    public List<GameWheelItem> getItemsByWheelId(Long wheelId) {
        return gameWheelItemMapper.selectByWheelId(wheelId);
    }

    @Override
    public GameWheelItem randomItem(Long wheelId) {
        List<GameWheelItem> items = getItemsByWheelId(wheelId);
        if (items.isEmpty()) {
            return null;
        }

        int totalWeight = items.stream().mapToInt(GameWheelItem::getWeight).sum();
        int random = new Random().nextInt(totalWeight) + 1;
        int current = 0;

        for (GameWheelItem item : items) {
            current += item.getWeight();
            if (random <= current) {
                return item;
            }
        }
        return items.get(0);
    }
}
