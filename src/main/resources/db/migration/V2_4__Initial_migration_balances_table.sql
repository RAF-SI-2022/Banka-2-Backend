CREATE TABLE IF NOT EXISTS `balances` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `amount` FLOAT  NOT NULL,
    `currency_id` BIGINT NOT NULL,
    `user_id` BIGINT NOT NULL,
    FOREIGN KEY (currency_id) REFERENCES currencies(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);
