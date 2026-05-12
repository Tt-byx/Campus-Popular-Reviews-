-- ============================================
-- 校圈社区 v2.0 数据库迁移脚本
-- 执行前请先备份数据库！
-- ============================================

-- 1. 扩展 user 表
ALTER TABLE `user` ADD COLUMN `bio` VARCHAR(200) DEFAULT NULL COMMENT '个人简介';
ALTER TABLE `user` ADD COLUMN `gender` TINYINT DEFAULT 0 COMMENT '0=未知, 1=男, 2=女';
ALTER TABLE `user` ADD COLUMN `campus` VARCHAR(50) DEFAULT NULL COMMENT '院系/校区';

-- 2. 扩展 post 表
ALTER TABLE `post` ADD COLUMN `like_count` INT DEFAULT 0 COMMENT '点赞数';
ALTER TABLE `post` ADD COLUMN `favorite_count` INT DEFAULT 0 COMMENT '收藏数';

-- 3. 创建点赞表
CREATE TABLE IF NOT EXISTS `like_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL COMMENT '点赞用户ID',
  `target_type` VARCHAR(20) NOT NULL COMMENT 'post 或 comment',
  `target_id` BIGINT NOT NULL COMMENT '被点赞的目标ID',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_target` (`user_id`, `target_type`, `target_id`),
  KEY `idx_target` (`target_type`, `target_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='点赞记录';

-- 4. 创建收藏表
CREATE TABLE IF NOT EXISTS `favorite` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL COMMENT '收藏用户ID',
  `post_id` BIGINT NOT NULL COMMENT '帖子ID',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_post` (`user_id`, `post_id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_post` (`post_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏记录';

-- 5. 创建关注表
CREATE TABLE IF NOT EXISTS `follow` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `follower_id` BIGINT NOT NULL COMMENT '关注者用户ID',
  `following_id` BIGINT NOT NULL COMMENT '被关注者用户ID',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_follow` (`follower_id`, `following_id`),
  KEY `idx_following` (`following_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='关注关系';

-- 6. 创建私信表
CREATE TABLE IF NOT EXISTS `message` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `sender_id` BIGINT NOT NULL COMMENT '发送者用户ID',
  `receiver_id` BIGINT NOT NULL COMMENT '接收者用户ID',
  `content` TEXT NOT NULL COMMENT '消息内容',
  `is_read` TINYINT(1) DEFAULT 0 COMMENT '是否已读',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_conversation` (`sender_id`, `receiver_id`, `created_at`),
  KEY `idx_receiver_read` (`receiver_id`, `is_read`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='私信消息';

-- 7. 创建通知表
CREATE TABLE IF NOT EXISTS `notification` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL COMMENT '接收者用户ID',
  `from_user_id` BIGINT DEFAULT NULL COMMENT '发送者用户ID（null为系统通知）',
  `type` VARCHAR(30) NOT NULL COMMENT 'like/comment/follow/system',
  `target_type` VARCHAR(20) DEFAULT NULL COMMENT 'post/comment/review_target',
  `target_id` BIGINT DEFAULT NULL COMMENT '目标ID',
  `content` VARCHAR(500) DEFAULT NULL COMMENT '通知内容',
  `is_read` TINYINT(1) DEFAULT 0 COMMENT '是否已读',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_read` (`user_id`, `is_read`, `created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知消息';

-- 8. 创建分享记录表
CREATE TABLE IF NOT EXISTS `share_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT DEFAULT NULL COMMENT '分享用户ID（null为匿名分享）',
  `post_id` BIGINT NOT NULL COMMENT '帖子ID',
  `share_type` VARCHAR(20) NOT NULL COMMENT 'link/qrcode/wechat/qq',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_post` (`post_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分享记录';
