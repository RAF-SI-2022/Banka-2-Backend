CREATE TABLE forex
(
    `id`                BIGINT AUTO_INCREMENT PRIMARY KEY,
    `from_currency_code` varchar(255) NOT NULL,
    `from_currency_name` varchar(255) NOT NULL,
    `to_currency_code`   varchar(255) NOT NULL,
    `to_currency_name`   varchar(255) NOT NULL,
    `exchange_rate`      varchar(255) NOT NULL,
    `last_refreshed`     varchar(255) NOT NULL,
    `time_zone`          varchar(255) NOT NULL,
    `bid_price`          varchar(255) NOT NULL,
    `ask_price`          varchar(255) NOT NULL

);