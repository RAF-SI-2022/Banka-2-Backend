CREATE TABLE IF NOT EXISTS `options`
(
    `id`                 BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `contract_symbol`    VARCHAR(100) NOT NULL,
    `stock_symbol`       VARCHAR(100) NOT NULL,
    `option_type`        VARCHAR(100) NOT NULL,
    `strike`             DOUBLE       NOT NULL,
    `implied_volatility` DOUBLE       NOT NULL,
    `price`              DOUBLE       NOT NULL,
    `expiration_date`    DATE         NOT NULL,
    `open_interest`      INTEGER      NOT NULL,
    `contract_size`      INTEGER      NOT NULL,
    `maintenance_margin` DOUBLE       NOT NULL,
    `bid`                DOUBLE       NOT NULL,
    `ask`                DOUBLE       NOT NULL,
    `change_price`       DOUBLE       NOT NULL,
    `percent_change`     DOUBLE       NOT NULL,
    `in_the_money`       BOOLEAN      NOT NULL
);