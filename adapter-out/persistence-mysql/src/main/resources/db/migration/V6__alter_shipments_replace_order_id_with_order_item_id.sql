-- V6: shipments 테이블 order_id/order_number → order_item_id 변경
-- Fulfillment V4 스펙: 배송 단위를 주문(orderId)에서 상품주문(orderItemId)으로 변경

-- 1. order_item_id 컬럼 추가
ALTER TABLE `shipments` ADD COLUMN `order_item_id` BIGINT NULL AFTER `shipment_number`;

-- 2. 기존 데이터 마이그레이션: order_id로 order_items.id 매핑
UPDATE `shipments` s
    JOIN `order_items` oi ON oi.order_id = s.order_id
SET s.order_item_id = oi.id
WHERE s.order_item_id IS NULL;

-- 3. NOT NULL 제약조건 설정
ALTER TABLE `shipments` MODIFY COLUMN `order_item_id` BIGINT NOT NULL;

-- 4. 기존 인덱스 삭제
DROP INDEX `idx_shipments_order_id` ON `shipments`;
DROP INDEX `idx_shipments_order_number` ON `shipments`;

-- 5. 기존 컬럼 삭제
ALTER TABLE `shipments` DROP COLUMN `order_id`;
ALTER TABLE `shipments` DROP COLUMN `order_number`;

-- 6. 새 인덱스 추가
CREATE INDEX `idx_shipments_order_item_id` ON `shipments` (`order_item_id`);
