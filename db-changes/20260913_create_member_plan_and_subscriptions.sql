CREATE TABLE `member_plans` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_time` datetime(6) DEFAULT NULL,
  `updated_time` datetime(6) DEFAULT NULL,
  `code` varchar(64) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `description` varchar(500) DEFAULT NULL,
  `price` decimal(38,2) DEFAULT NULL,
  `duration_days` int DEFAULT NULL,
  `granted_balance` decimal(38,2) DEFAULT NULL,
  `discount_percent` decimal(5,2) DEFAULT '0.00',
  `free_minutes` int DEFAULT '0',
  `max_vehicles` int DEFAULT '1',
  `is_active` bit(1) DEFAULT b'1',
  `status` int DEFAULT '1',
  `extra_features` json DEFAULT NULL,
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  `upload_by` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_member_plans_code` (`code`),
  CONSTRAINT `FK_member_plans_created_by` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `FK_member_plans_updated_by` FOREIGN KEY (`updated_by`) REFERENCES `users` (`id`),
  CONSTRAINT `FK_member_plans_upload_by` FOREIGN KEY (`upload_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `member_subscriptions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_time` datetime(6) DEFAULT NULL,
  `updated_time` datetime(6) DEFAULT NULL,
  `member_id` bigint NOT NULL,
  `plan_id` bigint NOT NULL,
  `start_date` datetime(6) DEFAULT NULL,
  `end_date` datetime(6) DEFAULT NULL,
  `initial_balance` decimal(38,2) DEFAULT NULL,
  `balance` decimal(38,2) DEFAULT NULL,
  `status` int DEFAULT '1',
  `version` bigint DEFAULT '0',
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  `upload_by` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `IDX_member_subscriptions_member_status_end` (`member_id`,`status`,`end_date`),
  KEY `IDX_member_subscriptions_plan` (`plan_id`),
  CONSTRAINT `FK_member_subscriptions_member` FOREIGN KEY (`member_id`) REFERENCES `members` (`id`),
  CONSTRAINT `FK_member_subscriptions_plan` FOREIGN KEY (`plan_id`) REFERENCES `member_plans` (`id`),
  CONSTRAINT `FK_member_subscriptions_created_by` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `FK_member_subscriptions_updated_by` FOREIGN KEY (`updated_by`) REFERENCES `users` (`id`),
  CONSTRAINT `FK_member_subscriptions_upload_by` FOREIGN KEY (`upload_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `member_balance_transactions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_time` datetime(6) DEFAULT NULL,
  `updated_time` datetime(6) DEFAULT NULL,
  `subscription_id` bigint NOT NULL,
  `member_id` bigint NOT NULL,
  `session_id` bigint DEFAULT NULL,
  `transaction_type` int DEFAULT NULL,
  `amount` decimal(38,2) DEFAULT NULL,
  `balance_after` decimal(38,2) DEFAULT NULL,
  `remark` varchar(500) DEFAULT NULL,
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  `upload_by` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `IDX_mbt_subscription_created` (`subscription_id`,`created_time`),
  KEY `IDX_mbt_member` (`member_id`),
  KEY `IDX_mbt_session` (`session_id`),
  CONSTRAINT `FK_mbt_subscription` FOREIGN KEY (`subscription_id`) REFERENCES `member_subscriptions` (`id`),
  CONSTRAINT `FK_mbt_member` FOREIGN KEY (`member_id`) REFERENCES `members` (`id`),
  CONSTRAINT `FK_mbt_session` FOREIGN KEY (`session_id`) REFERENCES `parking_sessions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

ALTER TABLE `members`
  ADD COLUMN `current_subscription_id` bigint DEFAULT NULL,
  ADD KEY `FK_members_current_subscription` (`current_subscription_id`),
  ADD CONSTRAINT `FK_members_current_subscription` FOREIGN KEY (`current_subscription_id`) REFERENCES `member_subscriptions` (`id`);

ALTER TABLE `payments`
  ADD COLUMN `subscription_id` bigint DEFAULT NULL,
  ADD KEY `FK_payments_subscription` (`subscription_id`),
  ADD CONSTRAINT `FK_payments_subscription` FOREIGN KEY (`subscription_id`) REFERENCES `member_subscriptions` (`id`);