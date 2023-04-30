ALTER TABLE `orders`
    ADD COLUMN `dtype` VARCHAR(255) NOT NULL,
    ADD COLUMN `future_name`    VARCHAR(50),
    ADD COLUMN `stock_limit`    INT,
    ADD COLUMN `stop`    INT,
    ADD COLUMN `all_or_none`    BOOLEAN,
    ADD COLUMN `margin`    BOOLEAN,
    ADD COLUMN `currency_code`    VARCHAR(5);



