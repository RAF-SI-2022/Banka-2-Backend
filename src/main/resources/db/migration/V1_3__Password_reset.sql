CREATE TABLE IF NOT EXISTS `password_reset_tokens`
(
    `id`              BIGINT      NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `token`           varchar(40) NOT NULL,
    `expiration_date` datetime    NOT NULL,
    `user_id`         bigint      NOT NULL,
    CONSTRAINT fk_password_reset_tokens_user foreign key (user_id) references users (id)
);