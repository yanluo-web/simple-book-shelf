-- 插入默认管理员账号（密码：123456，BCrypt 加密）
INSERT INTO sys_user (username, password, nickname, role, enabled)
VALUES ('admin', '$2a$10$kHrd7mDHc2Mh4X2YmzaEf.43NjfZ84CLLEaru9VIoUa5K.Rptp1K.', '管理员', 'ADMIN', TRUE);

-- 插入默认书架（关联管理员用户 ID=1，依赖 sys_user 第一条数据自增 ID=1）
INSERT INTO sys_shelf (shelf_name, description, create_user_id)
VALUES ('默认书架', '系统默认创建的书架，用于存储所有未分类书籍', 1);