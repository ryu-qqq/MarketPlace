-- V93: Shop에 credential 컬럼 추가 + SellerSalesChannel에 shopId 추가

-- Shop 테이블에 API 자격증명 컬럼 추가
ALTER TABLE shop ADD COLUMN channel_code VARCHAR(50) NULL AFTER status;
ALTER TABLE shop ADD COLUMN api_key VARCHAR(500) NULL AFTER channel_code;
ALTER TABLE shop ADD COLUMN api_secret VARCHAR(500) NULL AFTER api_key;
ALTER TABLE shop ADD COLUMN access_token VARCHAR(1000) NULL AFTER api_secret;
ALTER TABLE shop ADD COLUMN vendor_id VARCHAR(100) NULL AFTER access_token;

-- SellerSalesChannel 테이블에 shopId 컬럼 추가
ALTER TABLE seller_sales_channels ADD COLUMN shop_id BIGINT NOT NULL DEFAULT 0 AFTER display_name;

-- 인덱스 추가
CREATE INDEX idx_shop_channel_code ON shop (channel_code);
CREATE INDEX idx_seller_sales_channels_shop_id ON seller_sales_channels (shop_id);
