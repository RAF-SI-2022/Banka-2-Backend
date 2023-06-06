ALTER TABLE `balances`
    ADD COLUMN free     FLOAT NOT NULL,
    ADD COLUMN reserved FLOAT NOT NULL,
    ADD COLUMN type     ENUM ('CASH','MARGIN') NOT NULL;

ALTER TABLE `orders`
    DROP COLUMN `finished`,
    DROP COLUMN `type`,
    ADD COLUMN `user_id`       BIGINT NOT NULL,
    ADD COLUMN `trade_type`    ENUM('BUY', 'SELL') NOT NULL,
    MODIFY COLUMN `order_type`    ENUM('STOCK', 'FUTURE', 'FOREX') NOT NULL,
    MODIFY COLUMN `status`     ENUM('WAITING', 'DENIED', 'IN_PROGRESS', 'COMPLETE') NOT NULL,
    ADD FOREIGN KEY (user_id)  REFERENCES users (id);

CREATE TABLE IF NOT EXISTS `transactions`
(
    `id`          BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `balance_id`  BIGINT       NOT NULL,
    `timestamp`   TIMESTAMP    NOT NULL,
    `order_id`    BIGINT       NOT NULL,
    `user_id`     BIGINT       NOT NULL,
    `description` VARCHAR(100) NOT NULL,
    `currency_id` BIGINT       NOT NULL,
    `amount`      FLOAT        NOT NULL,
    `reserved`    FLOAT        NOT NULL,
    `status`        ENUM ('WAITING', 'DENIED', 'IN_PROGRESS', 'COMPLETE') NOT NULL,
    FOREIGN KEY (balance_id) REFERENCES balances (id),
    FOREIGN KEY (order_id) REFERENCES orders (id),
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (currency_id) REFERENCES currencies (id)
);

