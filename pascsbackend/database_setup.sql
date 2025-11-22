-- =============================================
-- PASCS DATABASE SETUP SCRIPT
-- Tạo tất cả bảng và dữ liệu mẫu
-- Chạy script này trong phpMyAdmin hoặc MySQL command line
-- =============================================

USE pascs_db;

-- Tắt foreign key checks để có thể tạo bảng theo thứ tự bất kỳ
SET FOREIGN_KEY_CHECKS = 0;

-- =============================================
-- XÓA TẤT CẢ BẢNG CŨ (NẾU CÓ)
-- =============================================
DROP TABLE IF EXISTS `notifications`;
DROP TABLE IF EXISTS `feedbacks`;
DROP TABLE IF EXISTS `documents`;
DROP TABLE IF EXISTS `appointments`;
DROP TABLE IF EXISTS `applications`;
DROP TABLE IF EXISTS `queues`;
DROP TABLE IF EXISTS `counters`;
DROP TABLE IF EXISTS `services`;
DROP TABLE IF EXISTS `system_configs`;
DROP TABLE IF EXISTS `roles`;
DROP TABLE IF EXISTS `users`;

-- =============================================
-- TẠO BẢNG USERS
-- =============================================
CREATE TABLE `users` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `username` VARCHAR(255) NOT NULL UNIQUE,
    `email` VARCHAR(255) NOT NULL UNIQUE,
    `password` VARCHAR(255) NOT NULL,
    `full_name` VARCHAR(255),
    `phone_number` VARCHAR(50),
    `address` VARCHAR(500),
    `priority_eligible` BOOLEAN DEFAULT FALSE,
    `enabled` BOOLEAN DEFAULT TRUE,
    `created_at` DATETIME,
    `role` VARCHAR(20) NOT NULL,
    PRIMARY KEY (`id`),
    INDEX `idx_username` (`username`),
    INDEX `idx_email` (`email`),
    INDEX `idx_role` (`role`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- TẠO BẢNG ROLES
-- =============================================
CREATE TABLE `roles` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(20),
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- TẠO BẢNG SERVICES
-- =============================================
CREATE TABLE `services` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `code` VARCHAR(255) NOT NULL UNIQUE,
    `name` VARCHAR(255) NOT NULL,
    `description` VARCHAR(1000),
    `required_documents` VARCHAR(1000),
    `processing_time` INT,
    `fee` DOUBLE,
    `status` VARCHAR(20) DEFAULT 'ACTIVE',
    `created_at` DATETIME,
    `updated_at` DATETIME,
    PRIMARY KEY (`id`),
    INDEX `idx_code` (`code`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- TẠO BẢNG COUNTERS
-- =============================================
CREATE TABLE `counters` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(255) NOT NULL,
    `location` VARCHAR(255) NOT NULL,
    `active` BOOLEAN DEFAULT TRUE,
    `current_staff_id` BIGINT,
    `current_queue_number` VARCHAR(50),
    PRIMARY KEY (`id`),
    FOREIGN KEY (`current_staff_id`) REFERENCES `users`(`id`) ON DELETE SET NULL,
    INDEX `idx_active` (`active`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- TẠO BẢNG APPLICATIONS
-- =============================================
CREATE TABLE `applications` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `application_code` VARCHAR(255) NOT NULL UNIQUE,
    `user_id` BIGINT NOT NULL,
    `service_id` BIGINT NOT NULL,
    `description` VARCHAR(2000),
    `status` VARCHAR(50) DEFAULT 'PENDING',
    `documents` VARCHAR(1000),
    `assigned_staff_id` BIGINT,
    `processing_step` INT DEFAULT 1,
    `current_step_name` VARCHAR(255) DEFAULT 'Tiếp nhận hồ sơ',
    `urgent` BOOLEAN DEFAULT FALSE,
    `contact_info` VARCHAR(500),
    `special_requirements` VARCHAR(1000),
    `submitted_at` DATETIME,
    `processed_at` DATETIME,
    `completed_at` DATETIME,
    `note` VARCHAR(2000),
    PRIMARY KEY (`id`),
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`service_id`) REFERENCES `services`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`assigned_staff_id`) REFERENCES `users`(`id`) ON DELETE SET NULL,
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_service_id` (`service_id`),
    INDEX `idx_status` (`status`),
    INDEX `idx_assigned_staff_id` (`assigned_staff_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- TẠO BẢNG APPOINTMENTS
-- =============================================
CREATE TABLE `appointments` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `appointment_code` VARCHAR(255) NOT NULL UNIQUE,
    `user_id` BIGINT NOT NULL,
    `service_id` BIGINT NOT NULL,
    `appointment_time` DATETIME NOT NULL,
    `end_time` DATETIME,
    `status` VARCHAR(50) DEFAULT 'SCHEDULED',
    `note` VARCHAR(1000),
    `assigned_staff_id` BIGINT,
    `created_at` DATETIME,
    `updated_at` DATETIME,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`service_id`) REFERENCES `services`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`assigned_staff_id`) REFERENCES `users`(`id`) ON DELETE SET NULL,
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_service_id` (`service_id`),
    INDEX `idx_status` (`status`),
    INDEX `idx_appointment_time` (`appointment_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- TẠO BẢNG QUEUES
-- =============================================
CREATE TABLE `queues` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `ticket_number` VARCHAR(255) NOT NULL UNIQUE,
    `user_id` BIGINT NOT NULL,
    `service_id` BIGINT NOT NULL,
    `status` VARCHAR(50) DEFAULT 'WAITING',
    `priority` BOOLEAN DEFAULT FALSE,
    `estimated_wait_time` INT,
    `counter_id` BIGINT,
    `assigned_staff_id` BIGINT,
    `note` VARCHAR(1000),
    `contact_preference` VARCHAR(100),
    `check_in_time` DATETIME,
    `called_time` DATETIME,
    `completed_time` DATETIME,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`service_id`) REFERENCES `services`(`id`) ON DELETE CASCADE,
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_service_id` (`service_id`),
    INDEX `idx_status` (`status`),
    INDEX `idx_ticket_number` (`ticket_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- TẠO BẢNG DOCUMENTS
-- =============================================
CREATE TABLE `documents` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `file_name` VARCHAR(255) NOT NULL,
    `file_path` VARCHAR(500) NOT NULL,
    `file_type` VARCHAR(100),
    `file_size` BIGINT,
    `document_type` VARCHAR(100),
    `application_id` BIGINT,
    `uploaded_by` BIGINT,
    `uploaded_at` DATETIME,
    `description` VARCHAR(1000),
    PRIMARY KEY (`id`),
    FOREIGN KEY (`uploaded_by`) REFERENCES `users`(`id`) ON DELETE SET NULL,
    INDEX `idx_application_id` (`application_id`),
    INDEX `idx_uploaded_by` (`uploaded_by`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- TẠO BẢNG NOTIFICATIONS
-- =============================================
CREATE TABLE `notifications` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `title` VARCHAR(255) NOT NULL,
    `message` VARCHAR(1000),
    `type` VARCHAR(50) DEFAULT 'INFO',
    `status` VARCHAR(50) DEFAULT 'UNREAD',
    `user_id` BIGINT NOT NULL,
    `action_url` VARCHAR(500),
    `related_entity_type` VARCHAR(100),
    `related_entity_id` BIGINT,
    `created_at` DATETIME,
    `read_at` DATETIME,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_status` (`status`),
    INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- TẠO BẢNG FEEDBACKS
-- =============================================
CREATE TABLE `feedbacks` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `service_id` BIGINT,
    `rating` INT NOT NULL,
    `comment` VARCHAR(2000),
    `created_at` DATETIME,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`service_id`) REFERENCES `services`(`id`) ON DELETE SET NULL,
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_service_id` (`service_id`),
    INDEX `idx_rating` (`rating`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- TẠO BẢNG SYSTEM_CONFIGS
-- =============================================
CREATE TABLE `system_configs` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `config_key` VARCHAR(255) NOT NULL UNIQUE,
    `config_value` VARCHAR(1000),
    `description` VARCHAR(500),
    `config_group` VARCHAR(100) DEFAULT 'SYSTEM',
    `editable` BOOLEAN DEFAULT TRUE,
    `visible` BOOLEAN DEFAULT TRUE,
    `created_at` DATETIME,
    `updated_at` DATETIME,
    PRIMARY KEY (`id`),
    INDEX `idx_config_key` (`config_key`),
    INDEX `idx_config_group` (`config_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bật lại foreign key checks
SET FOREIGN_KEY_CHECKS = 1;

-- =============================================
-- INSERT DỮ LIỆU MẪU
-- =============================================

-- Insert Roles
INSERT INTO `roles` (`name`) VALUES 
('ADMIN'),
('STAFF'),
('CITIZEN');

-- Users sẽ được tạo tự động bởi DataInitializer khi ứng dụng khởi động
-- DataInitializer sẽ tạo password hash đúng bằng BCrypt
-- Không cần insert users ở đây

-- Insert Services
INSERT INTO `services` (`code`, `name`, `description`, `required_documents`, `processing_time`, `fee`, `status`, `created_at`, `updated_at`) VALUES
('CCCD', 'Cấp căn cước công dân', 'Cấp mới, cấp đổi, cấp lại căn cước công dân', 'CMND cũ (nếu có), Ảnh 4x6, Tờ khai theo mẫu', 7, 0.0, 'ACTIVE', NOW(), NOW()),
('HK', 'Đăng ký hộ khẩu', 'Đăng ký thường trú, tạm trú, thay đổi hộ khẩu', 'Giấy tờ nhà ở, CMND/CCCD, Ảnh 3x4', 3, 0.0, 'ACTIVE', NOW(), NOW()),
('KS', 'Khai sinh', 'Đăng ký khai sinh cho trẻ em', 'Giấy chứng sinh, CMND của cha mẹ, Giấy đăng ký kết hôn', 1, 0.0, 'ACTIVE', NOW(), NOW()),
('TT', 'Đăng ký tạm trú', 'Đăng ký tạm trú cho người ngoại tỉnh', 'CMND/CCCD, Giấy tờ thuê nhà, Ảnh 3x4', 2, 0.0, 'ACTIVE', NOW(), NOW()),
('KH', 'Đăng ký kết hôn', 'Đăng ký kết hôn theo quy định pháp luật', 'CMND/CCCD, Giấy xác nhận tình trạng hôn nhân, Ảnh 3x4', 5, 0.0, 'ACTIVE', NOW(), NOW());

-- Counters sẽ được tạo tự động bởi DataInitializer sau khi users được tạo
-- Không cần insert counters ở đây vì cần tham chiếu đến users

-- Insert System Configs
INSERT INTO `system_configs` (`config_key`, `config_value`, `description`, `config_group`, `editable`, `visible`, `created_at`, `updated_at`) VALUES
('system.name', 'PASCS - Hệ thống Dịch vụ Công', 'Tên hệ thống', 'SYSTEM', TRUE, TRUE, NOW(), NOW()),
('system.version', '1.0.0', 'Phiên bản hệ thống', 'SYSTEM', FALSE, TRUE, NOW(), NOW()),
('queue.max_wait_time', '60', 'Thời gian chờ tối đa (phút)', 'QUEUE', TRUE, TRUE, NOW(), NOW()),
('queue.priority_enabled', 'true', 'Bật chế độ ưu tiên', 'QUEUE', TRUE, TRUE, NOW(), NOW());

-- =============================================
-- THÔNG BÁO HOÀN TẤT
-- =============================================
SELECT 'Database setup completed successfully!' AS message;
SELECT 'Total tables created: 11' AS info;
SELECT 'Sample data inserted: Roles (3), Services (5), Configs (4)' AS info;
SELECT 'NOTE: Users and Counters will be created automatically by DataInitializer when the application starts' AS note;

-- =============================================
-- THÔNG TIN ĐĂNG NHẬP MẪU
-- =============================================
-- Sau khi chạy ứng dụng, DataInitializer sẽ tạo các users sau:
SELECT 
    '===== SAMPLE CREDENTIALS (sẽ được tạo bởi DataInitializer) =====' AS info
UNION ALL
SELECT 'ADMIN:     admin / admin123'
UNION ALL
SELECT 'STAFF:     staff / staff123'
UNION ALL
SELECT 'STAFF 2:   staff2 / staff123'
UNION ALL
SELECT 'CITIZEN:   citizen / citizen123'
UNION ALL
SELECT 'CITIZEN 2: hongphuc / 123456'
UNION ALL
SELECT 'CITIZEN 3: thanhlong / 123456';

