package com.example.bookshelf.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 转盘选项表实体类
 */
@Data
public class GameWheelItem {
    /**
     * 选项ID
     */
    private Long id;
    /**
     * 所属转盘ID
     */
    private Long wheelId;
    /**
     * 选项内容（如：罚酒3杯）
     */
    private String content;
    /**
     * 类型：惩罚/奖励/互动/整蛊
     */
    private String type;
    /**
     * 权重（越大概率越高）
     */
    private Integer weight;
    /**
     * 转盘颜色
     */
    private String color;
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
