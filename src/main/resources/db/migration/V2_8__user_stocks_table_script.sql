CREATE TABLE IF NOT EXISTS `users_stocks`
(
    `id`              BIGINT  NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `user_id`         BIGINT  NOT NULL,
    `stock_id`        BIGINT  NOT NULL,
    `amount`          INTEGER NOT NULL,
    `amount_for_sale` INTEGER NOT NULL
);