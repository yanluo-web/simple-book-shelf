-- 插入默认管理员账号（密码：123456，BCrypt 加密）
INSERT INTO sys_user (username, password, nickname, role, enabled)
VALUES ('admin', '$2a$10$kHrd7mDHc2Mh4X2YmzaEf.43NjfZ84CLLEaru9VIoUa5K.Rptp1K.', '管理员', 'ADMIN', TRUE);

-- 插入默认书架（关联管理员用户 ID=1，依赖 sys_user 第一条数据自增 ID=1）
INSERT INTO sys_shelf (shelf_name, description, create_user_id)
VALUES ('默认书架', '系统默认创建的书架，用于存储所有未分类书籍', 1);

-- 插入默认转盘
INSERT INTO game_wheel (name, description)
VALUES
    ('酒桌惩罚转盘', '朋友喝酒专用，罚酒、代喝、整蛊'),
    ('情侣互动转盘', '情侣约会小游戏，甜蜜惩罚');

-- 酒桌惩罚转盘数据
INSERT INTO game_wheel_item (wheel_id, content, type, weight, color) VALUES
    (1, '罚酒2杯', '惩罚', 20, '#FF5733'),
    (1, '罚酒3杯', '惩罚', 15, '#E74C3C'),
    (1, '指定1人代喝1杯', '互动', 15, '#3498DB'),
    (1, '全场干杯', '整蛊', 10, '#9B59B6'),
    (1, '免罚一次', '奖励', 5, '#2ECC71'),
    (1, '自罚1杯+指定1人陪喝', '惩罚', 15, '#F39C12'),
    (1, '左边的人喝1杯', '整蛊', 10, '#1ABC9C'),
    (1, '右边的人喝1杯', '整蛊', 10, '#34495E');

-- 情侣互动转盘数据
INSERT INTO game_wheel_item (wheel_id, content, type, weight, color) VALUES
    (2, '拥抱10秒', '互动', 20, '#FF69B4'),
    (2, '亲一下额头', '互动', 15, '#FF1493'),
    (2, '唱一句情歌', '惩罚', 15, '#FF4500'),
    (2, '夸对方3个优点', '奖励', 10, '#FFB6C1'),
    (2, '答应对方一个小要求', '奖励', 5, '#FFC0CB');