ALTER TABLE inbound_products
    ADD COLUMN retry_count INT NOT NULL DEFAULT 0 AFTER raw_payload;
