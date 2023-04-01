CREATE TABLE IF NOT EXISTS `balances` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `amount` FLOAT  NOT NULL,
    `currency_id` BIGINT NOT NULL,
    `user_id` BIGINT NOT NULL,
    FOREIGN KEY (currency_id) REFERENCES currencies(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS `users_balances` (
    `user_id` BIGINT NOT NULL,
    `balance_id` BIGINT NOT NULL,
    PRIMARY KEY (user_id, balance_id),
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (balance_id) REFERENCES balances (id)
);
