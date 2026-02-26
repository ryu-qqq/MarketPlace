ALTER TABLE shop ADD COLUMN sales_channel_id BIGINT NULL COMMENT '판매채널 ID' AFTER id;
UPDATE shop SET sales_channel_id = 1 WHERE sales_channel_id IS NULL;
ALTER TABLE shop MODIFY COLUMN sales_channel_id BIGINT NOT NULL COMMENT '판매채널 ID';
ALTER TABLE shop DROP INDEX uq_shop_name;
ALTER TABLE shop DROP INDEX uq_account_id;
ALTER TABLE shop ADD UNIQUE KEY uq_sc_account (sales_channel_id, account_id);
ALTER TABLE shop ADD INDEX idx_shop_sc (sales_channel_id);
