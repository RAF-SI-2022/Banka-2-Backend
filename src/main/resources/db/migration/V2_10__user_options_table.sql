CREATE TABLE IF NOT EXISTS `users_options`
(

    `id`              BIGINT      NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `user_id`         BIGINT,
    `option_id`       BIGINT      NOT NULL,
    `premium`         DOUBLE,
    `amount`          INTEGER     NOT NULL,
    `type`            VARCHAR(15) NOT NULL,
    `expiration_date` DATE        NOT NULL,
    `strike`          DOUBLE      NOT NULL,

    CONSTRAINT fk_users_options_users FOREIGN KEY (user_id) REFERENCES `users` (id),
    CONSTRAINT fk_users_options_options FOREIGN KEY (option_id) REFERENCES `options` (id)
);