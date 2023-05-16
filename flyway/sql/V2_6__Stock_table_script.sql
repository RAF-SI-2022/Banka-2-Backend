CREATE TABLE IF NOT EXISTS `stocks`
(

    `id`                 BIGINT         NOT NULL AUTO_INCREMENT,
    `symbol`             VARCHAR(50)    NOT NULL,
    `company_name`       VARCHAR(255)   NOT NULL,
    `outstanding_shares` BIGINT         NOT NULL,
    `dividend_yield`     DECIMAL(20, 6) NOT NULL,
    `price_value`        DECIMAL(10, 2),
    `open_value`         DECIMAL(10, 2),
    `low_value`          DECIMAL(10, 2),
    `high_value`         DECIMAL(10, 2),
    `change_value`       DECIMAL(10, 2),
    `previous_close`     DECIMAL(10, 2),
    `volume_value`       BIGINT,
    `last_updated`       DATE,
    `change_percent`     VARCHAR(50),
    `website_url`        VARCHAR(255),
    `exchange_id`        BIGINT         NOT NULL,

    CONSTRAINT fk_stocks_exchange FOREIGN KEY (exchange_id) REFERENCES exchange (id),
    PRIMARY KEY (`id`)
);