CREATE TABLE IF NOT EXISTS `stock_history`
(

    `id`           BIGINT         NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `open_value`   DECIMAL(10, 2) NOT NULL,
    `high_value`   DECIMAL(10, 2) NOT NULL,
    `low_value`    DECIMAL(10, 2) NOT NULL,
    `close_value`  DECIMAL(10, 2) NOT NULL,
    `volume_value` BIGINT         NOT NULL,
    `on_date`      DATETIME DEFAULT NULL,
    `stock_id`     BIGINT         NOT NULL,
    `type`         ENUM ('ONE_DAY','FIVE_DAYS','DAILY'),
    CONSTRAINT fk_stock_history_stocks foreign key (stock_id) references stocks (id)
);