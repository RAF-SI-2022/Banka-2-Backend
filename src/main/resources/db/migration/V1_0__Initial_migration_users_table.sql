CREATE TABLE IF NOT EXISTS `users`
(
    `id`            BIGINT      NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `email`         varchar(50) NOT NULL,
    `password`      varchar(20) NOT NULL,
    `first_name`    varchar(20) NOT NULL,
    `last_name`     varchar(20) NOT NULL,
    `jmbg`          varchar(13) NOT NULL,
    `pozicija`      varchar(20) NOT NULL,
    `broj_telefona` varchar(20) NOT NULL,
    `aktivan`       BOOLEAN,
    `daily_limit`   DOUBLE      ,
    `default_daily_limit`   DOUBLE
);
