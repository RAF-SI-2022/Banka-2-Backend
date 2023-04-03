CREATE TABLE IF NOT EXISTS `users_stocks` (

    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NOT NULL,
    `stock_id` BIGINT NOT NULL,
#     PRIMARY KEY (user_id, stock_id),
    CONSTRAINT fk_users_stocks_users foreign key (user_id) references users (id),
    CONSTRAINT fk_users_stocks_stocks foreign key (stock_id) references stocks (id),
    `amount` INTEGER NOT NULL
);