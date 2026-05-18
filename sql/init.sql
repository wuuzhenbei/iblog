-- iBlog 智能社交博客平台 - 数据库初始化脚本
-- MySQL 8.0+

CREATE DATABASE IF NOT EXISTS `iblog_db` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `iblog_db`;

-- 1. 用户表
CREATE TABLE `users` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `username` VARCHAR(50) NOT NULL UNIQUE,
  `phone` VARCHAR(20) UNIQUE,
  `password_hash` CHAR(64) NOT NULL,
  `role` ENUM('normal','advanced','circle_admin','super_admin') DEFAULT 'normal',
  `status` ENUM('active','frozen','deleted') DEFAULT 'active',
  `remember_token` VARCHAR(128),
  `last_login_ip` VARCHAR(45),
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. 用户资料表
CREATE TABLE `user_profiles` (
  `user_id` INT PRIMARY KEY,
  `nickname` VARCHAR(50) NOT NULL,
  `avatar_url` VARCHAR(255),
  `gender` ENUM('男','女','保密') DEFAULT '保密',
  `birthday` DATE,
  `region` VARCHAR(100),
  `signature` VARCHAR(200),
  `interests` VARCHAR(255),
  `bg_image_url` VARCHAR(255),
  `motto` VARCHAR(200),
  `level` INT DEFAULT 1,
  `points` INT DEFAULT 0,
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. 隐私设置表
CREATE TABLE `privacy_settings` (
  `user_id` INT PRIMARY KEY,
  `hide_online` TINYINT DEFAULT 0,
  `visit_permission` ENUM('all','friends','none') DEFAULT 'all',
  `dynamic_scope` ENUM('all','friends','self') DEFAULT 'all',
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. 博文表
CREATE TABLE `blogs` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `user_id` INT NOT NULL,
  `content` TEXT NOT NULL,
  `images` TEXT,
  `mood_tag` VARCHAR(50),
  `scene_tag` VARCHAR(50),
  `location` VARCHAR(100),
  `visibility` ENUM('public','followers','friends','private') DEFAULT 'public',
  `is_pinned` TINYINT DEFAULT 0,
  `status` ENUM('draft','published','scheduled','archived','deleted') DEFAULT 'published',
  `schedule_time` DATETIME,
  `view_count` INT DEFAULT 0,
  `like_count` INT DEFAULT 0,
  `comment_count` INT DEFAULT 0,
  `forward_count` INT DEFAULT 0,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
  INDEX `idx_user_status` (`user_id`, `status`),
  INDEX `idx_created` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. 评论表（楼中楼）
CREATE TABLE `comments` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `blog_id` INT NOT NULL,
  `user_id` INT NOT NULL,
  `parent_id` INT DEFAULT NULL,
  `reply_to_user_id` INT DEFAULT NULL,
  `content` TEXT NOT NULL,
  `like_count` INT DEFAULT 0,
  `status` ENUM('normal','deleted') DEFAULT 'normal',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`blog_id`) REFERENCES `blogs`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`parent_id`) REFERENCES `comments`(`id`) ON DELETE CASCADE,
  INDEX `idx_blog` (`blog_id`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 6. 互动表
CREATE TABLE `interactions` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `user_id` INT NOT NULL,
  `blog_id` INT NOT NULL,
  `type` ENUM('like','dislike','favorite','forward'),
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY `user_blog_type` (`user_id`,`blog_id`,`type`),
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`blog_id`) REFERENCES `blogs`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 7. 关注关系表
CREATE TABLE `follows` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `follower_id` INT NOT NULL,
  `following_id` INT NOT NULL,
  `status` ENUM('following','mutual','blocked') DEFAULT 'following',
  `group_name` VARCHAR(50),
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY `follow_pair` (`follower_id`,`following_id`),
  FOREIGN KEY (`follower_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`following_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
  INDEX `idx_following` (`following_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 8. 访客记录表
CREATE TABLE `visit_records` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `visitor_id` INT NOT NULL,
  `visited_user_id` INT NOT NULL,
  `visited_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`visitor_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`visited_user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
  INDEX `idx_visited` (`visited_user_id`, `visited_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 9. 私信表
CREATE TABLE `messages` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `sender_id` INT NOT NULL,
  `receiver_id` INT NOT NULL,
  `content` TEXT NOT NULL,
  `content_type` ENUM('text','image','emoji') DEFAULT 'text',
  `is_read` TINYINT DEFAULT 0,
  `is_recalled` TINYINT DEFAULT 0,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`sender_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`receiver_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
  INDEX `idx_conversation` (`sender_id`, `receiver_id`, `created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 10. 圈子表
CREATE TABLE `circles` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `name` VARCHAR(100) NOT NULL,
  `description` TEXT,
  `category` ENUM('娱乐','情感','游戏','美食','旅行','职场','其他'),
  `creator_id` INT,
  `is_official` TINYINT DEFAULT 0,
  `member_count` INT DEFAULT 0,
  `status` ENUM('active','frozen') DEFAULT 'active',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`creator_id`) REFERENCES `users`(`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 11. 圈子成员表
CREATE TABLE `circle_members` (
  `circle_id` INT NOT NULL,
  `user_id` INT NOT NULL,
  `role` ENUM('member','admin') DEFAULT 'member',
  `joined_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`circle_id`,`user_id`),
  FOREIGN KEY (`circle_id`) REFERENCES `circles`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 12. 热搜表
CREATE TABLE `hot_trends` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `keyword` VARCHAR(100) NOT NULL,
  `heat` INT DEFAULT 0,
  `related_blogs` TEXT,
  `is_manual` TINYINT DEFAULT 0,
  `status` ENUM('active','removed') DEFAULT 'active',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 13. 签到表
CREATE TABLE `sign_records` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `user_id` INT NOT NULL,
  `sign_date` DATE NOT NULL,
  `points_earned` INT DEFAULT 0,
  UNIQUE KEY `user_date` (`user_id`,`sign_date`),
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 14. 举报表
CREATE TABLE `reports` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `reporter_id` INT NOT NULL,
  `target_type` ENUM('blog','comment','user'),
  `target_id` INT NOT NULL,
  `reason` VARCHAR(255),
  `status` ENUM('pending','handled','dismissed') DEFAULT 'pending',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`reporter_id`) REFERENCES `users`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 默认管理员账号 (密码: admin123)
INSERT INTO `users` (`username`, `phone`, `password_hash`, `role`, `status`)
VALUES ('admin', '13800000000', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'super_admin', 'active');

INSERT INTO `user_profiles` (`user_id`, `nickname`, `level`)
VALUES (1, '系统管理员', 99);

-- ============================================================
-- 扩展：邮件验证 + 密码重置 + 收藏夹
-- ============================================================

-- users 表新增字段
ALTER TABLE `users` ADD COLUMN `email` VARCHAR(100) DEFAULT NULL AFTER `phone`;
ALTER TABLE `users` ADD COLUMN `reset_token` VARCHAR(128) DEFAULT NULL AFTER `remember_token`;
ALTER TABLE `users` ADD COLUMN `reset_token_expire` DATETIME DEFAULT NULL AFTER `reset_token`;

-- 收藏夹表（Session 购物车概念对应）
CREATE TABLE `favorites` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `user_id` INT NOT NULL,
  `blog_id` INT NOT NULL,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY `user_blog` (`user_id`, `blog_id`),
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`blog_id`) REFERENCES `blogs`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
