-- 报价系统数据库初始化脚本

-- 用户表
CREATE TABLE IF NOT EXISTS `users` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `username` VARCHAR(50) NOT NULL UNIQUE,
  `password` VARCHAR(255) NOT NULL,
  `full_name` VARCHAR(100),
  `email` VARCHAR(100),
  `role` VARCHAR(20) NOT NULL DEFAULT 'SALES' COMMENT 'ADMIN, SALES, APPROVER, VIEWER',
  `enabled` TINYINT(1) NOT NULL DEFAULT 1,
  `created_at` DATETIME,
  `updated_at` DATETIME,
  INDEX `idx_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 客户表
CREATE TABLE IF NOT EXISTS `customers` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `company_name` VARCHAR(200) NOT NULL,
  `contact_name` VARCHAR(100),
  `contact_phone` VARCHAR(50),
  `contact_email` VARCHAR(100),
  `address` VARCHAR(500),
  `city` VARCHAR(100),
  `province` VARCHAR(100),
  `country` VARCHAR(100) DEFAULT '中国',
  `postal_code` VARCHAR(20),
  `tax_id` VARCHAR(50),
  `notes` TEXT,
  `created_at` DATETIME,
  `updated_at` DATETIME,
  INDEX `idx_company_name` (`company_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户表';

-- 产品表
CREATE TABLE IF NOT EXISTS `products` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `sku` VARCHAR(100) NOT NULL UNIQUE,
  `name` VARCHAR(200) NOT NULL,
  `description` TEXT,
  `unit` VARCHAR(20) DEFAULT '个',
  `default_price` DECIMAL(18, 2) NOT NULL DEFAULT 0.00,
  `tax_rate` DECIMAL(5, 2) DEFAULT 13.00 COMMENT '默认税率(%)',
  `enabled` TINYINT(1) NOT NULL DEFAULT 1,
  `created_at` DATETIME,
  `updated_at` DATETIME,
  INDEX `idx_sku` (`sku`),
  INDEX `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='产品表';

-- 套餐表
CREATE TABLE IF NOT EXISTS `packages` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `code` VARCHAR(100) NOT NULL UNIQUE,
  `name` VARCHAR(200) NOT NULL,
  `description` TEXT,
  `currency` VARCHAR(10) DEFAULT 'CNY',
  `discount` DECIMAL(5, 2) DEFAULT 0.00 COMMENT '整体折扣(%)',
  `tax_rate` DECIMAL(5, 2) DEFAULT 13.00 COMMENT '默认税率(%)',
  `enabled` TINYINT(1) NOT NULL DEFAULT 1,
  `created_at` DATETIME,
  `updated_at` DATETIME,
  INDEX `idx_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='套餐表';

-- 套餐明细表
CREATE TABLE IF NOT EXISTS `package_items` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `package_id` BIGINT NOT NULL,
  `product_id` BIGINT NOT NULL,
  `product_name` VARCHAR(200),
  `product_sku` VARCHAR(100),
  `quantity` DECIMAL(18, 2) NOT NULL DEFAULT 1.00,
  `unit_price` DECIMAL(18, 2),
  `discount` DECIMAL(5, 2) DEFAULT 0.00,
  `tax_rate` DECIMAL(5, 2),
  `note` VARCHAR(500),
  FOREIGN KEY (`package_id`) REFERENCES `packages`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`product_id`) REFERENCES `products`(`id`),
  INDEX `idx_package_id` (`package_id`),
  INDEX `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='套餐明细表';

-- 报价单表
CREATE TABLE IF NOT EXISTS `quotations` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `quote_number` VARCHAR(50) NOT NULL UNIQUE,
  `title` VARCHAR(200) NOT NULL,
  `customer_id` BIGINT NOT NULL,
  `customer_name` VARCHAR(200),
  `created_by` BIGINT,
  `created_by_name` VARCHAR(100),
  `currency` VARCHAR(10) DEFAULT 'CNY',
  `subtotal` DECIMAL(18, 2) DEFAULT 0.00,
  `discount_amount` DECIMAL(18, 2) DEFAULT 0.00,
  `tax_amount` DECIMAL(18, 2) DEFAULT 0.00,
  `total` DECIMAL(18, 2) DEFAULT 0.00,
  `status` VARCHAR(20) NOT NULL DEFAULT 'DRAFT' COMMENT 'DRAFT, SUBMITTED, APPROVED, REJECTED, CANCELLED',
  `issue_date` DATE,
  `expiry_date` DATE,
  `package_id` BIGINT,
  `notes` TEXT,
  `created_at` DATETIME,
  `updated_at` DATETIME,
  FOREIGN KEY (`customer_id`) REFERENCES `customers`(`id`),
  FOREIGN KEY (`created_by`) REFERENCES `users`(`id`),
  FOREIGN KEY (`package_id`) REFERENCES `packages`(`id`),
  INDEX `idx_quote_number` (`quote_number`),
  INDEX `idx_customer_id` (`customer_id`),
  INDEX `idx_status` (`status`),
  INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报价单表';

-- 报价明细表
CREATE TABLE IF NOT EXISTS `quotation_items` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `quotation_id` BIGINT NOT NULL,
  `line_number` INT NOT NULL,
  `product_id` BIGINT,
  `product_name` VARCHAR(200),
  `product_sku` VARCHAR(100),
  `description` TEXT,
  `quantity` DECIMAL(18, 2) NOT NULL DEFAULT 1.00,
  `unit` VARCHAR(20),
  `unit_price` DECIMAL(18, 2) NOT NULL DEFAULT 0.00,
  `discount` DECIMAL(18, 2) DEFAULT 0.00,
  `discount_amount` DECIMAL(18, 2) DEFAULT 0.00,
  `tax_rate` DECIMAL(5, 2) DEFAULT 13.00,
  `line_subtotal` DECIMAL(18, 2) DEFAULT 0.00,
  `line_tax` DECIMAL(18, 2) DEFAULT 0.00,
  `line_total` DECIMAL(18, 2) DEFAULT 0.00,
  FOREIGN KEY (`quotation_id`) REFERENCES `quotations`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`product_id`) REFERENCES `products`(`id`),
  INDEX `idx_quotation_id` (`quotation_id`),
  INDEX `idx_line_number` (`quotation_id`, `line_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报价明细表';

-- 审批记录表
CREATE TABLE IF NOT EXISTS `approval_records` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `quotation_id` BIGINT NOT NULL,
  `approver_id` BIGINT,
  `approver_name` VARCHAR(100),
  `action` VARCHAR(20) NOT NULL COMMENT 'SUBMIT, APPROVE, REJECT, CANCEL',
  `comment` TEXT,
  `action_time` DATETIME NOT NULL,
  FOREIGN KEY (`quotation_id`) REFERENCES `quotations`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`approver_id`) REFERENCES `users`(`id`),
  INDEX `idx_quotation_id` (`quotation_id`),
  INDEX `idx_action_time` (`action_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审批记录表';

-- 附件表
CREATE TABLE IF NOT EXISTS `attachments` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `quotation_id` BIGINT NOT NULL,
  `file_name` VARCHAR(255) NOT NULL,
  `original_file_name` VARCHAR(255) NOT NULL,
  `file_path` VARCHAR(500) NOT NULL,
  `file_type` VARCHAR(100),
  `file_size` BIGINT,
  `uploaded_by` VARCHAR(100),
  `uploaded_at` DATETIME NOT NULL,
  FOREIGN KEY (`quotation_id`) REFERENCES `quotations`(`id`) ON DELETE CASCADE,
  INDEX `idx_quotation_id` (`quotation_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='附件表';

-- 系统设置表
CREATE TABLE IF NOT EXISTS `system_settings` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `setting_key` VARCHAR(100) NOT NULL UNIQUE,
  `setting_value` TEXT,
  `description` VARCHAR(500),
  `updated_at` DATETIME,
  INDEX `idx_setting_key` (`setting_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统设置表';

-- 插入默认系统设置
INSERT INTO `system_settings` (`setting_key`, `setting_value`, `description`, `updated_at`) VALUES
('default_tax_rate', '13', '默认税率(%)', NOW()),
('default_currency', 'CNY', '默认货币', NOW()),
('company_name', '示例公司', '公司名称', NOW()),
('company_address', '示例地址', '公司地址', NOW()),
('company_phone', '400-123-4567', '公司电话', NOW()),
('company_email', 'info@example.com', '公司邮箱', NOW())
ON DUPLICATE KEY UPDATE `updated_at` = NOW();

-- 插入默认管理员用户 (密码: admin123)
INSERT INTO `users` (`username`, `password`, `full_name`, `email`, `role`, `enabled`, `created_at`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7it8DXLOK', '系统管理员', 'admin@example.com', 'ADMIN', 1, NOW())
ON DUPLICATE KEY UPDATE `updated_at` = NOW();

-- 插入示例数据（可选）
INSERT INTO `products` (`sku`, `name`, `description`, `unit`, `default_price`, `tax_rate`, `enabled`, `created_at`) VALUES
('PROD001', '产品A', '产品A的描述', '个', 100.00, 13.00, 1, NOW()),
('PROD002', '产品B', '产品B的描述', '套', 200.00, 13.00, 1, NOW()),
('PROD003', '产品C', '产品C的描述', '台', 500.00, 13.00, 1, NOW())
ON DUPLICATE KEY UPDATE `updated_at` = NOW();

INSERT INTO `customers` (`company_name`, `contact_name`, `contact_phone`, `contact_email`, `address`, `city`, `province`, `created_at`) VALUES
('示例客户公司', '张总', '13800138000', 'zhang@example.com', '示例街道123号', '北京', '北京', NOW())
ON DUPLICATE KEY UPDATE `updated_at` = NOW();

