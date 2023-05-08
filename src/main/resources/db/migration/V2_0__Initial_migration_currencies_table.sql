CREATE TABLE IF NOT EXISTS `currencies`
(
    `id`              BIGINT      NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `currency_name`   varchar(50) NOT NULL,
    `currency_code`   varchar(3)  NOT NULL,
    `currency_symbol` varchar(20) NOT NULL,
    `polity`          varchar(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS `inflations`
(
    `id`             BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `year`           INT    NOT NULL,
    `inflation_rate` FLOAT  NOT NULL,
    `currency_id`    BIGINT NOT NULL,
    FOREIGN KEY (currency_id) REFERENCES currencies (id)
);