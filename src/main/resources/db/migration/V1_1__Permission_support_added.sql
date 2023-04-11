CREATE TABLE IF NOT EXISTS `permissions`
(
    `id`              BIGINT      NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `permission_name` varchar(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS `users_permissions`
(
    `user_id`       BIGINT NOT NULL,
    `permission_id` BIGINT NOT NULL,
    PRIMARY KEY (user_id, permission_id),
    CONSTRAINT fk_users_permissions_users foreign key (user_id) references users (id),
    CONSTRAINT fk_users_permissions_permissions foreign key (permission_id) references permissions (id)
);