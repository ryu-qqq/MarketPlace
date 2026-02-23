ALTER TABLE product_profiles
    ADD COLUMN description_content_hash VARCHAR(64) NULL AFTER error_message;
