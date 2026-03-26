-- 판매자 부담 할인액 컬럼 추가 (정산용)
ALTER TABLE inbound_order_items
    ADD COLUMN seller_burden_discount_amount INT NOT NULL DEFAULT 0 AFTER discount_amount;

ALTER TABLE order_items
    ADD COLUMN seller_burden_discount_amount INT NOT NULL DEFAULT 0 AFTER discount_amount;
