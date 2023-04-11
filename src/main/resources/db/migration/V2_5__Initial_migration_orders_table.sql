CREATE TABLE IF NOT EXISTS `orders`
(
    `id`            BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `order_type`    VARCHAR(255) NOT NULL,
    `type`          VARCHAR(255) NOT NULL,
    `symbol`        VARCHAR(255) NOT NULL,
    `amount`        INT          NOT NULL,
    `price`         DOUBLE       NOT NULL,
    `status`        VARCHAR(255) NOT NULL,
    `finished`      BOOLEAN,
    `last_modified` varchar(255) NOT NULL
);
