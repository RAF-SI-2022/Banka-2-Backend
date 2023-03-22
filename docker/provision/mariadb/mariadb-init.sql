/**
  MariaDB initialization shell script (SQL).
 */

# prod
CREATE DATABASE IF NOT EXISTS `prod`;

# test
DROP DATABASE IF EXISTS `test`;
CREATE DATABASE IF NOT EXISTS `test`;

# dev
CREATE DATABASE IF NOT EXISTS `dev`;

# user
CREATE USER IF NOT EXISTS 'root'@'localhost' IDENTIFIED BY 'local';
GRANT ALL PRIVILEGES ON *.* TO 'root'@'%';