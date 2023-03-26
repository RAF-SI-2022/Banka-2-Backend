CREATE TABLE exchange (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        exchange_name VARCHAR(255),
        acronym VARCHAR(255),
        mic_code VARCHAR(255) NOT NULL,
        polity VARCHAR(255),
        currency VARCHAR(255),
        time_zone VARCHAR(255),
        open_time VARCHAR(255),
        close_time VARCHAR(255)
--         CONSTRAINT unique_acronym UNIQUE (acronym),
--         CONSTRAINT unique_mic_code UNIQUE (mic_code)
);

CREATE TABLE exchange_calendar (
        exchange_id BIGINT NOT NULL,
        calendar_value VARCHAR(255),
        FOREIGN KEY (exchange_id) REFERENCES exchange (id)
);