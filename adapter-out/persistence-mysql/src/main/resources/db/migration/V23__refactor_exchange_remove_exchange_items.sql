-- Exchange 리팩토링: ExchangeItem 제거, ExchangeClaim에 order_item_id + exchange_qty + seller_id 통합
-- ExchangeClaim 1건 = OrderItem 1건의 1회 교환 요청
-- ExchangeTarget → ExchangeOption 리네이밍 + original 필드 추가

-- 1. exchange_claims 테이블에 order_item_id, exchange_qty, seller_id 컬럼 추가
ALTER TABLE exchange_claims
    ADD COLUMN order_item_id VARCHAR(36) NULL AFTER claim_number,
    ADD COLUMN exchange_qty INT NOT NULL DEFAULT 1 AFTER order_item_id,
    ADD COLUMN seller_id BIGINT NOT NULL DEFAULT 0 AFTER exchange_qty;

-- 2. 기존 exchange_items 데이터를 exchange_claims로 마이그레이션
UPDATE exchange_claims ec
    INNER JOIN exchange_items ei ON ei.exchange_claim_id = ec.id
SET ec.order_item_id = ei.order_item_id,
    ec.exchange_qty  = ei.exchange_qty;

-- 3. order_item_id NOT NULL 제약 추가
ALTER TABLE exchange_claims
    MODIFY COLUMN order_item_id VARCHAR(36) NOT NULL;

-- 4. order_id 컬럼 제거 (ExchangeClaim은 더 이상 Order가 아닌 OrderItem 기준)
ALTER TABLE exchange_claims
    DROP INDEX idx_exchange_claims_order_id,
    DROP COLUMN order_id;

-- 5. order_item_id 인덱스 추가
CREATE INDEX idx_exchange_claims_order_item_id ON exchange_claims (order_item_id);

-- 6. seller_id 인덱스 추가
CREATE INDEX idx_exchange_claims_seller_id ON exchange_claims (seller_id);

-- 7. ExchangeOption original 필드 추가
ALTER TABLE exchange_claims
    ADD COLUMN original_product_id BIGINT NULL AFTER reason_detail,
    ADD COLUMN original_sku_code VARCHAR(50) NULL AFTER original_product_id;

-- 8. exchange_items 테이블 삭제
DROP TABLE IF EXISTS exchange_items;
