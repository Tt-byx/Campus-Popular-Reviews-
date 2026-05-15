-- 校圈初始数据脚本

-- 插入默认超级管理员账户（密码: admin123，BCrypt加密）
INSERT IGNORE INTO `user` (`username`, `password`, `role`, `status`) VALUES 
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'super_admin', 'active');

-- 插入一些示例违禁词
INSERT IGNORE INTO `banned_word` (`word`) VALUES 
('垃圾'),
('骗子'),
('滚蛋'),
('混蛋');