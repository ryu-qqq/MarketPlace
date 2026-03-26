-- order_items 테이블 구조 개선
-- 1. delivery_status → order_item_status 컬럼명 변경 (실제 OrderItemStatus 저장 용도)
-- 2. 배송 관련 컬럼 제거 (shipments 테이블에서 관리)
-- 3. 정산 관련 컬럼 제거 (정산 도메인으로 분리 예정)
-- 4. seller_id, brand_id 외래키 제거 (seller_name, brand_name 스냅샷으로 대체)

-- 컬럼명 변경: delivery_status → order_item_status
ALTER TABLE order_items CHANGE COLUMN delivery_status order_item_status VARCHAR(20) NOT NULL DEFAULT 'READY';

-- 배송 컬럼 제거
ALTER TABLE order_items DROP COLUMN shipment_company_code;
ALTER TABLE order_items DROP COLUMN invoice;
ALTER TABLE order_items DROP COLUMN shipment_completed_date;

-- 정산 컬럼 제거
ALTER TABLE order_items DROP COLUMN commission_rate;
ALTER TABLE order_items DROP COLUMN fee;
ALTER TABLE order_items DROP COLUMN expectation_settlement_amount;
ALTER TABLE order_items DROP COLUMN settlement_amount;
ALTER TABLE order_items DROP COLUMN share_ratio;
ALTER TABLE order_items DROP COLUMN expected_settlement_day;
ALTER TABLE order_items DROP COLUMN settlement_day;

-- 외래키 컬럼 제거 (스냅샷 이름으로 대체)
ALTER TABLE order_items DROP INDEX idx_order_items_seller_id;
ALTER TABLE order_items DROP COLUMN seller_id;
ALTER TABLE order_items DROP COLUMN brand_id;
