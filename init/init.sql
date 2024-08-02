CREATE DATABASE IF NOT EXISTS csvmanager;
USE csvmanager;
DROP USER IF EXISTS 'csvimporter'@'%';
DROP USER IF EXISTS 'csvimporter'@'localhost';
CREATE USER 'csvimporter'@'%' IDENTIFIED BY 'csvimporter';
GRANT ALL PRIVILEGES ON csvmanager.* TO 'csvimporter'@'%';
FLUSH PRIVILEGES;
