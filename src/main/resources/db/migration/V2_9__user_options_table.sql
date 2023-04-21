CREATE TABLE IF NOT EXISTS `users_options`
(

    `id`        BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `user_id`   BIGINT NOT NULL,
    `option_id` BIGINT NOT NULL,
    `user_price` DOUBLE,

    CONSTRAINT fk_users_options_users FOREIGN KEY (user_id) REFERENCES `users` (id),
    CONSTRAINT fk_users_options_options FOREIGN KEY (option_id) REFERENCES `options` (id)
);