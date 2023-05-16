CREATE TABLE `future_table`
(
    `id`                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    `future_name`        VARCHAR(255) NOT NULL,
    `contract_size`      INT          NOT NULL,
    `contract_unit`      VARCHAR(255) NOT NULL,
    `maintenance_margin` INT          NOT NULL,
    `type`               VARCHAR(255),
    `settlement_date`    VARCHAR(255),
    `for_sale`           BOOLEAN,
    `user_id`            BIGINT
);
