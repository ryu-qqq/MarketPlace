-- V10: order_items.id를 UUIDv7(varchar)로 변경, FK 테이블 order_item_id 컬럼 동기 변경
-- order_cancels, order_claims, shipments, shipment_outboxes의 order_item_id를 varchar(36)으로 변경

-- ============================================================
-- 1. order_items: id bigint → varchar(36) UUID
-- ============================================================

-- 1-1. 임시 UUID 컬럼 추가
ALTER TABLE `order_items`
    ADD COLUMN `new_id` varchar(36) COLLATE utf8mb4_unicode_ci DEFAULT NULL AFTER `id`;

-- 1-2. 기존 id 값을 UUID 형식으로 변환하여 임시 컬럼에 저장
UPDATE `order_items` SET `new_id` = UUID() WHERE `new_id` IS NULL;

-- ============================================================
-- 2. FK 테이블들: order_item_id_new 컬럼 추가 및 마이그레이션
-- ============================================================

-- 2-1. shipments
ALTER TABLE `shipments` ADD COLUMN `order_item_id_new` varchar(36) COLLATE utf8mb4_unicode_ci DEFAULT NULL AFTER `order_item_id`;
UPDATE `shipments` s
    JOIN `order_items` oi ON s.order_item_id = oi.id
SET s.order_item_id_new = oi.new_id;

-- 2-2. order_cancels
ALTER TABLE `order_cancels` ADD COLUMN `order_item_id_new` varchar(36) COLLATE utf8mb4_unicode_ci DEFAULT NULL AFTER `order_item_id`;
UPDATE `order_cancels` oc
    JOIN `order_items` oi ON oc.order_item_id = oi.id
SET oc.order_item_id_new = oi.new_id;

-- 2-3. order_claims
ALTER TABLE `order_claims` ADD COLUMN `order_item_id_new` varchar(36) COLLATE utf8mb4_unicode_ci DEFAULT NULL AFTER `order_item_id`;
UPDATE `order_claims` oc
    JOIN `order_items` oi ON oc.order_item_id = oi.id
SET oc.order_item_id_new = oi.new_id;

-- 2-4. shipment_outboxes
ALTER TABLE `shipment_outboxes` ADD COLUMN `order_item_id_new` varchar(36) COLLATE utf8mb4_unicode_ci DEFAULT NULL AFTER `order_item_id`;
UPDATE `shipment_outboxes` so
    JOIN `order_items` oi ON so.order_item_id = oi.id
SET so.order_item_id_new = oi.new_id;

-- ============================================================
-- 3. order_items: PK 변경 (id bigint → varchar)
-- ============================================================

ALTER TABLE `order_items`
    MODIFY COLUMN `id` bigint NOT NULL,
    DROP PRIMARY KEY;

ALTER TABLE `order_items`
    DROP COLUMN `id`;

ALTER TABLE `order_items`
    CHANGE COLUMN `new_id` `id` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '상품주문 ID (UUIDv7)',
    ADD PRIMARY KEY (`id`);

-- ============================================================
-- 4. FK 테이블들: 기존 컬럼 삭제, 새 컬럼으로 교체
-- ============================================================

-- 4-1. shipments
DROP INDEX `idx_shipments_order_item_id` ON `shipments`;
ALTER TABLE `shipments` DROP COLUMN `order_item_id`;
ALTER TABLE `shipments` CHANGE COLUMN `order_item_id_new` `order_item_id` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '상품주문 ID (FK)';
CREATE INDEX `idx_shipments_order_item_id` ON `shipments` (`order_item_id`);

-- 4-2. order_cancels
DROP INDEX `idx_order_cancels_order_item_id` ON `order_cancels`;
ALTER TABLE `order_cancels` DROP COLUMN `order_item_id`;
ALTER TABLE `order_cancels` CHANGE COLUMN `order_item_id_new` `order_item_id` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '상품주문 ID (FK)';
CREATE INDEX `idx_order_cancels_order_item_id` ON `order_cancels` (`order_item_id`);

-- 4-3. order_claims
DROP INDEX `idx_order_claims_order_item_id` ON `order_claims`;
ALTER TABLE `order_claims` DROP COLUMN `order_item_id`;
ALTER TABLE `order_claims` CHANGE COLUMN `order_item_id_new` `order_item_id` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '상품주문 ID (FK)';
CREATE INDEX `idx_order_claims_order_item_id` ON `order_claims` (`order_item_id`);

-- 4-4. shipment_outboxes
DROP INDEX `idx_shipment_outboxes_order_item_id` ON `shipment_outboxes`;
ALTER TABLE `shipment_outboxes` DROP COLUMN `order_item_id`;
ALTER TABLE `shipment_outboxes` CHANGE COLUMN `order_item_id_new` `order_item_id` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '상품주문 ID (FK)';
CREATE INDEX `idx_shipment_outboxes_order_item_id` ON `shipment_outboxes` (`order_item_id`);
