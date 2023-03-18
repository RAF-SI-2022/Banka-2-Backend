/**
  Users database (MariaDB) initialization shell script (SQL).
 */

# prod
CREATE DATABASE IF NOT EXISTS `users-prod`;

# test
DROP DATABASE IF EXISTS `users-test`;
CREATE DATABASE IF NOT EXISTS `users-test`;

# dev
CREATE DATABASE IF NOT EXISTS `users-dev`;

# user
CREATE USER IF NOT EXISTS 'root'@'localhost' IDENTIFIED BY 'local';
GRANT ALL PRIVILEGES ON *.* TO 'root'@'%';