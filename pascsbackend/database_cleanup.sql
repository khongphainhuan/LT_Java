-- Script để xóa tất cả bảng trong database pascs_db
-- Chạy script này trong phpMyAdmin hoặc MySQL command line trước khi chạy ứng dụng
-- Sau đó Hibernate sẽ tự động tạo lại tất cả bảng từ entity definitions

USE pascs_db;

-- Tắt foreign key checks để có thể xóa bảng
SET FOREIGN_KEY_CHECKS = 0;

-- Xóa tất cả các bảng (nếu tồn tại)
DROP TABLE IF EXISTS `application_history`;
DROP TABLE IF EXISTS `applications`;
DROP TABLE IF EXISTS `appointments`;
DROP TABLE IF EXISTS `counters`;
DROP TABLE IF EXISTS `documents`;
DROP TABLE IF EXISTS `feedbacks`;
DROP TABLE IF EXISTS `notifications`;
DROP TABLE IF EXISTS `queues`;
DROP TABLE IF EXISTS `queue_tickets`;
DROP TABLE IF EXISTS `roles`;
DROP TABLE IF EXISTS `services`;
DROP TABLE IF EXISTS `system_configs`;
DROP TABLE IF EXISTS `users`;

-- Bật lại foreign key checks
SET FOREIGN_KEY_CHECKS = 1;

-- Thông báo
SELECT 'All tables dropped successfully. Now run the Spring Boot application to create tables automatically.' AS message;

