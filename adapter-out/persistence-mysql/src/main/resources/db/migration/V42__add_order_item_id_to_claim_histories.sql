-- claim_histories에 order_item_id 추가, claim_id nullable 변경
ALTER TABLE claim_histories
    ADD COLUMN order_item_id VARCHAR(36) NULL COMMENT '주문상품 ID (OrderItem 기준 전체 이력 조회용)' AFTER claim_id,
    MODIFY COLUMN claim_id VARCHAR(36) NULL COMMENT '클레임 ID (ORDER 타입일 때 null)';

-- order_item_id 기준 조회 인덱스
CREATE INDEX idx_claim_histories_order_item ON claim_histories (order_item_id);
