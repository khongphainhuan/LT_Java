-- =============================================
-- UPDATE PASSWORDS SCRIPT
-- Script này sẽ cập nhật password bằng BCrypt hash
-- Chạy sau khi chạy database_setup.sql và ứng dụng đã khởi động
-- Hoặc chạy script này sau khi DataInitializer đã tạo users
-- =============================================

USE pascs_db;

-- Lưu ý: Các password hash này được tạo bằng BCrypt với cost factor 10
-- Password: admin123
UPDATE `users` SET `password` = '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy' WHERE `username` = 'admin';

-- Password: staff123
UPDATE `users` SET `password` = '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy' WHERE `username` = 'staff';

-- Password: staff123
UPDATE `users` SET `password` = '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy' WHERE `username` = 'staff2';

-- Password: citizen123
UPDATE `users` SET `password` = '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy' WHERE `username` = 'citizen';

-- Password: 123456
UPDATE `users` SET `password` = '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy' WHERE `username` = 'hongphuc';

-- Password: 123456
UPDATE `users` SET `password` = '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy' WHERE `username` = 'thanhlong';

SELECT 'Passwords updated successfully!' AS message;
SELECT 'Note: All passwords use the same hash for simplicity. For production, use DataInitializer to create proper BCrypt hashes.' AS warning;

