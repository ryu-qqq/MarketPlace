-- Cancel에 seller_id 추가 (소유권 검증용 비정규화)
ALTER TABLE cancels
    ADD COLUMN seller_id BIGINT NOT NULL DEFAULT 0 AFTER order_item_id;

-- 기존 데이터는 order_items에서 seller_id를 가져와 업데이트
UPDATE cancels c
    INNER JOIN order_items oi ON oi.id = c.order_item_id
SET c.seller_id = oi.seller_id;

CREATE INDEX idx_cancels_seller_id ON cancels (seller_id);
