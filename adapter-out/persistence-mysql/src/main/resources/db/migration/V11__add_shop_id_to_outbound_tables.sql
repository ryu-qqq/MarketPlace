-- V11: outbound_products, outbound_sync_outboxes 테이블에 shop_id 컬럼 추가
-- 하나의 SalesChannel에 여러 Shop이 존재할 수 있으므로 shop_id로 구분 필요

-- 1. outbound_products 테이블에 shop_id 추가
ALTER TABLE `outbound_products`
    ADD COLUMN `shop_id` bigint NOT NULL DEFAULT 0 COMMENT '샵 ID' AFTER `sales_channel_id`;

-- 기존 unique key 변경: (product_group_id, sales_channel_id) → (product_group_id, sales_channel_id, shop_id)
ALTER TABLE `outbound_products`
    DROP INDEX `uk_pg_channel`,
    ADD UNIQUE KEY `uk_pg_channel_shop` (`product_group_id`, `sales_channel_id`, `shop_id`);

-- shop_id 인덱스 추가
ALTER TABLE `outbound_products`
    ADD INDEX `idx_outbound_products_shop_id` (`shop_id`);

-- 2. outbound_sync_outboxes 테이블에 shop_id 추가
ALTER TABLE `outbound_sync_outboxes`
    ADD COLUMN `shop_id` bigint NOT NULL DEFAULT 0 COMMENT '샵 ID' AFTER `sales_channel_id`;

-- shop_id 인덱스 추가
ALTER TABLE `outbound_sync_outboxes`
    ADD INDEX `idx_outbound_sync_outboxes_shop_id` (`shop_id`);
