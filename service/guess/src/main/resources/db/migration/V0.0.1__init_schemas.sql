CREATE TABLE IF NOT EXISTS `user` (
  `id` bigint NOT NULL,
  `nickname` varchar(45) DEFAULT NULL,
  `gender` tinyint DEFAULT NULL,
  `avatar` blob DEFAULT NULL,
  `status` tinyint NOT NULL DEFAULT '0',
  `created_at` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  `updated_at` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  CONSTRAINT `USER_PK` PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `user_weapp` (
  `id` bigint NOT NULL,
  `app_id` varchar(45) DEFAULT NULL,
  `open_id` varchar(45) DEFAULT NULL,
  `union_id` varchar(45) DEFAULT NULL,
  `session_key` varchar(45) DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  `created_at` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  `updated_at` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  CONSTRAINT `USER_WEAPP_PK` PRIMARY KEY (`id`),
  CONSTRAINT `USER_WEAPP_UNION_ID_UK` UNIQUE KEY (`union_id`)
);
CREATE INDEX `USER_WEAPP_OPEN_ID_K` ON `user_weapp` (`open_id`);
CREATE INDEX `USER_WEAPP_USER_ID_K` ON `user_weapp` (`user_id`);

CREATE TABLE IF NOT EXISTS `user_token` (
  `id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `refresh_token` varchar(45) DEFAULT NULL,
  `created_at` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  `updated_at` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  CONSTRAINT `USER_TOKEN_PK` PRIMARY KEY (`id`),
  CONSTRAINT `USER_TOKEN_REFRESH_TOKEN_UK` UNIQUE KEY (`refresh_token`)
);

CREATE TABLE IF NOT EXISTS `user_extra` (
  `id` bigint NOT NULL,
  `moment` int NOT NULL DEFAULT '0',
  `follow` int NOT NULL DEFAULT '0',
  `created_at` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  `updated_at` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  CONSTRAINT `USER_EXTRA_PK` PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `moment` (
  `id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `content` varchar(200) DEFAULT NULL,
  `option1` varchar(20) DEFAULT NULL,
  `option2` varchar(20) DEFAULT NULL,
  `option3` varchar(20) DEFAULT NULL,
  `option4` varchar(20) DEFAULT NULL,
  `answer` int NOT NULL DEFAULT '0',
  `status` tinyint NOT NULL DEFAULT '0',
  `follow` int NOT NULL DEFAULT '0',
  `comment` int NOT NULL DEFAULT '0',
  `like` int NOT NULL DEFAULT '0',
  `report` int NOT NULL DEFAULT '0',
  `created_at` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  `updated_at` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  CONSTRAINT `MOMENT_PK` PRIMARY KEY (`id`)
);
CREATE INDEX `MOMENT_USER_ID_K` ON `moment` (`user_id`);
CREATE INDEX `MOMENT_STATUS_K` ON `moment` (`status`);

CREATE TABLE IF NOT EXISTS `moment_action` (
  `id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `moment_id` bigint NOT NULL,
  `correct` tinyint NOT NULL DEFAULT '0',
  `followed` tinyint NOT NULL DEFAULT '0',
  `liked` tinyint NOT NULL DEFAULT '0',
  `reported` tinyint NOT NULL DEFAULT '0',
  `created_at` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  `updated_at` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  CONSTRAINT `MOMENT_ACTION_PK` PRIMARY KEY (`id`),
  CONSTRAINT `MOMENT_ACTION_USER_ID_MOMENT_ID_UK` UNIQUE KEY (`user_id`, `moment_id`)
);

CREATE TABLE IF NOT EXISTS `comment` (
  `id` bigint NOT NULL,
  `moment_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `to_user_id` bigint NULL,
  `status` tinyint NOT NULL DEFAULT '0',
  `content` varchar(200) NOT NULL,
  `like` int NOT NULL DEFAULT '0',
  `report` int NOT NULL DEFAULT '0',
  `created_at` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  `updated_at` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  CONSTRAINT `COMMENT_PK` PRIMARY KEY (`id`)
);
CREATE INDEX `COMMENT_STATUS_K` ON `comment` (`status`);

CREATE TABLE IF NOT EXISTS `comment_action` (
  `id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `comment_id` bigint NOT NULL,
  `liked` tinyint NOT NULL DEFAULT '0',
  `reported` tinyint NOT NULL DEFAULT '0',
  `created_at` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  `updated_at` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  CONSTRAINT `COMMENT_ACTION_PK` PRIMARY KEY (`id`),
  CONSTRAINT `COMMENT_ACTION_USER_ID_COMMENT_ID_UK` UNIQUE KEY (`user_id`, `comment_id`)
);

CREATE TABLE IF NOT EXISTS `puzzle_action` (
  `id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `level` int NOT NULL DEFAULT '0',
  `increase_times` int NOT NULL DEFAULT '0',
  `daily_attempts` int NOT NULL DEFAULT '0',
  `reset_at` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  `created_at` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  `updated_at` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  CONSTRAINT `PUZZLE_ACTION_PK` PRIMARY KEY (`id`),
  CONSTRAINT `PUZZLE_ACTION_UK` UNIQUE KEY (`user_id`)
);
CREATE INDEX `PUZZLE_ACTION_LEVEL_K` ON `puzzle_action` (`level`);
