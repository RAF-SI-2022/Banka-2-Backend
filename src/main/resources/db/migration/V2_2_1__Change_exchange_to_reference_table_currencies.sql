ALTER TABLE exchange
 ADD COLUMN currency_id BIGINT;

UPDATE exchange SET currency_id = (SELECT id FROM currencies WHERE currency_name = exchange.currency);

ALTER TABLE exchange ADD FOREIGN KEY (currency_id) REFERENCES currencies(id);

ALTER TABLE exchange
  DROP COLUMN currency;