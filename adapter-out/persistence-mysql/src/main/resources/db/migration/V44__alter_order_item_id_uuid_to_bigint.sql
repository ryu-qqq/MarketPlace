-- ============================================================
-- V44: order_items.id UUID(varchar(36)) → BIGINT AUTO_INCREMENT
--
-- 배경: 레거시 주문 PK(long)와 호환성 확보 + 레거시 스키마 의존 제거
-- 전략: FK 없음 (인덱스만 사용) → TRUNCATE 후 ALTER
-- Stage: 백업 데이터는 별도 스크립트로 재삽입
-- Prod: 주문 데이터 미존재 (레거시 이관 전)
-- ============================================================

-- Step 1: 의존 테이블 TRUNCATE (FK 없으므로 순서 무관)
TRUNCATE TABLE shipment_outboxes;
TRUNCATE TABLE cancel_outboxes;
TRUNCATE TABLE refund_outboxes;
TRUNCATE TABLE exchange_outboxes;
TRUNCATE TABLE order_item_histories;
TRUNCATE TABLE claim_histories;
TRUNCATE TABLE shipments;
TRUNCATE TABLE cancels;
TRUNCATE TABLE refund_claims;
TRUNCATE TABLE exchange_claims;
TRUNCATE TABLE external_order_item_mappings;
TRUNCATE TABLE order_cancels;
TRUNCATE TABLE order_claims;
TRUNCATE TABLE settlement_entries;
TRUNCATE TABLE order_items;

-- Step 2: order_items PK 변경 (varchar(36) → BIGINT AUTO_INCREMENT)
ALTER TABLE order_items MODIFY COLUMN id BIGINT NOT NULL AUTO_INCREMENT;

-- Step 3: 의존 테이블 order_item_id 컬럼 타입 변경
ALTER TABLE shipments MODIFY COLUMN order_item_id BIGINT NOT NULL;
ALTER TABLE cancels MODIFY COLUMN order_item_id BIGINT NOT NULL;
ALTER TABLE refund_claims MODIFY COLUMN order_item_id BIGINT NOT NULL;
ALTER TABLE exchange_claims MODIFY COLUMN order_item_id BIGINT NOT NULL;
ALTER TABLE shipment_outboxes MODIFY COLUMN order_item_id BIGINT NOT NULL;
ALTER TABLE cancel_outboxes MODIFY COLUMN order_item_id BIGINT NOT NULL;
ALTER TABLE refund_outboxes MODIFY COLUMN order_item_id BIGINT NOT NULL;
ALTER TABLE exchange_outboxes MODIFY COLUMN order_item_id BIGINT NOT NULL;
ALTER TABLE external_order_item_mappings MODIFY COLUMN order_item_id BIGINT NOT NULL;
ALTER TABLE order_item_histories MODIFY COLUMN order_item_id BIGINT NOT NULL;
ALTER TABLE claim_histories MODIFY COLUMN order_item_id BIGINT NOT NULL;
ALTER TABLE order_cancels MODIFY COLUMN order_item_id BIGINT NOT NULL;
ALTER TABLE order_claims MODIFY COLUMN order_item_id BIGINT NOT NULL;
ALTER TABLE settlement_entries MODIFY COLUMN order_item_id BIGINT NOT NULL;
