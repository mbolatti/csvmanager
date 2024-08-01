CREATE DATABASE IF NOT EXISTS csvmanager;
DROP USER IF EXISTS 'csvimporter'@'%';
-- Drop user if exists
DROP USER IF EXISTS 'csvimporter'@'localhost';
CREATE USER 'csvimporter'@'%' IDENTIFIED BY 'csvimporter';
GRANT ALL PRIVILEGES ON csvmanager.* TO 'csvimporter'@'%';
USE csvmanager;
FLUSH PRIVILEGES;
