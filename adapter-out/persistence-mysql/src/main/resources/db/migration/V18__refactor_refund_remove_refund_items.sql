-- Refund 리팩토링: RefundItem 제거, RefundClaim에 order_item_id + refund_qty + seller_id 통합
-- RefundClaim 1건 = OrderItem 1건의 1회 환불 요청

-- 1. refund_claims 테이블에 order_item_id, refund_qty, seller_id 컬럼 추가
ALTER TABLE refund_claims
    ADD COLUMN order_item_id VARCHAR(36) NULL AFTER order_id,
    ADD COLUMN refund_qty INT NOT NULL DEFAULT 1 AFTER order_item_id,
    ADD COLUMN seller_id BIGINT NOT NULL DEFAULT 0 AFTER refund_qty;

-- 2. 기존 refund_items 데이터를 refund_claims로 마이그레이션
UPDATE refund_claims rc
    INNER JOIN refund_items ri ON ri.refund_claim_id = rc.id
SET rc.order_item_id = ri.order_item_id,
    rc.refund_qty    = ri.refund_qty;

-- 3. order_item_id NOT NULL 제약 추가
ALTER TABLE refund_claims
    MODIFY COLUMN order_item_id VARCHAR(36) NOT NULL;

-- 4. order_id 컬럼 제거 (RefundClaim은 더 이상 Order가 아닌 OrderItem 기준)
ALTER TABLE refund_claims
    DROP INDEX idx_refund_claims_order_id,
    DROP COLUMN order_id;

-- 5. order_item_id, seller_id 인덱스 추가
CREATE INDEX idx_refund_claims_order_item_id ON refund_claims (order_item_id);
CREATE INDEX idx_refund_claims_seller_id ON refund_claims (seller_id);

-- 6. refund_items 테이블 삭제
DROP TABLE IF EXISTS refund_items;
