ALTER TABLE product_option_mappings
    ADD COLUMN deleted    TINYINT(1) NOT NULL DEFAULT 0,
    ADD COLUMN deleted_at DATETIME(6) NULL;
