-- 校圈数据库初始化脚本（H2测试版本）
-- 校园点评社区平台

-- 用户表
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(255) NOT NULL COMMENT '密码哈希值',
  `avatar` varchar(500) DEFAULT NULL COMMENT '头像URL',
  `signature` varchar(200) DEFAULT NULL COMMENT '个性签名',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
  `role` varchar(20) DEFAULT 'user' COMMENT '角色: user/admin/super_admin',
  `status` varchar(20) DEFAULT 'active' COMMENT '状态: active/muted',
  `bio` varchar(500) DEFAULT NULL COMMENT '个人简介',
  `gender` tinyint DEFAULT '0' COMMENT '性别: 0=未知, 1=男, 2=女',
  `campus` varchar(100) DEFAULT NULL COMMENT '院系/校区',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
);

-- 帖子表
CREATE TABLE `post` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '帖子ID',
  `user_id` bigint NOT NULL COMMENT '发布者ID',
  `title` varchar(200) NOT NULL COMMENT '标题',
  `content` text NOT NULL COMMENT '内容',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  `updated_at` datetime DEFAULT NULL COMMENT '更新时间',
  `image_url` varchar(500) DEFAULT NULL COMMENT '图片URL',
  `tag` varchar(50) DEFAULT NULL COMMENT '分类标签',
  `view_count` int DEFAULT '0' COMMENT '浏览量',
  `like_count` int DEFAULT '0' COMMENT '点赞数',
  `favorite_count` int DEFAULT '0' COMMENT '收藏数',
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_post_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
);

-- 评论对象表
CREATE TABLE `review_target` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '评论对象ID',
  `post_id` bigint NOT NULL COMMENT '所属帖子ID',
  `target_name` varchar(100) NOT NULL COMMENT '评论对象名称',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_review_target_post` FOREIGN KEY (`post_id`) REFERENCES `post` (`id`)
);

-- 评论用户表
CREATE TABLE `comment_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `review_target_id` bigint NOT NULL COMMENT '评论对象ID',
  `username` varchar(50) NOT NULL COMMENT '评论者用户名',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '评论创建时间',
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_comment_review_target` FOREIGN KEY (`review_target_id`) REFERENCES `review_target` (`id`)
);

-- 评论内容表
CREATE TABLE `comment_content` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `review_target_id` bigint NOT NULL COMMENT '评论对象ID',
  `comment_id` bigint NOT NULL COMMENT '评论ID(关联comment_user.id)',
  `content` varchar(500) NOT NULL COMMENT '评论内容',
  `score` tinyint DEFAULT NULL COMMENT '评分(0-5)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_comment_content_comment_id` (`comment_id`),
  CONSTRAINT `fk_comment_content_comment` FOREIGN KEY (`comment_id`) REFERENCES `comment_user` (`id`),
  CONSTRAINT `fk_comment_content_review_target` FOREIGN KEY (`review_target_id`) REFERENCES `review_target` (`id`)
);

-- 违禁词表
CREATE TABLE `banned_word` (
  `id` int NOT NULL AUTO_INCREMENT,
  `word` varchar(100) NOT NULL COMMENT '违禁词',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `word` (`word`)
);

-- 收藏表
CREATE TABLE `favorite` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `post_id` bigint NOT NULL COMMENT '帖子ID',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_post` (`user_id`,`post_id`)
);

-- 关注表
CREATE TABLE `follow` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `follower_id` bigint NOT NULL COMMENT '关注者ID',
  `following_id` bigint NOT NULL COMMENT '被关注者ID',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '关注时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_follow` (`follower_id`,`following_id`)
);

-- 点赞记录表
CREATE TABLE `like_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `target_type` varchar(20) NOT NULL COMMENT '目标类型: post/comment',
  `target_id` bigint NOT NULL COMMENT '目标ID',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_target` (`user_id`,`target_type`,`target_id`)
);

-- 私信表
CREATE TABLE `message` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `sender_id` bigint NOT NULL COMMENT '发送者ID',
  `receiver_id` bigint NOT NULL COMMENT '接收者ID',
  `content` text NOT NULL COMMENT '消息内容',
  `is_read` tinyint(1) DEFAULT '0' COMMENT '是否已读: 0=未读, 1=已读',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
  PRIMARY KEY (`id`)
);

-- 通知表
CREATE TABLE `notification` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '通知ID',
  `user_id` bigint NOT NULL COMMENT '接收用户ID',
  `from_user_id` bigint DEFAULT NULL COMMENT '触发用户ID',
  `type` varchar(30) NOT NULL COMMENT '类型: like/comment/follow/system',
  `target_type` varchar(20) DEFAULT NULL COMMENT '目标类型: post/comment',
  `target_id` bigint DEFAULT NULL COMMENT '目标ID',
  `content` varchar(500) DEFAULT NULL COMMENT '通知内容',
  `is_read` tinyint(1) DEFAULT '0' COMMENT '是否已读: 0=未读, 1=已读',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
);

-- 分享记录表
CREATE TABLE `share_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `user_id` bigint DEFAULT NULL COMMENT '用户ID',
  `post_id` bigint NOT NULL COMMENT '帖子ID',
  `share_type` varchar(20) NOT NULL COMMENT '分享方式: link/wechat/qq',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '分享时间',
  PRIMARY KEY (`id`)
);