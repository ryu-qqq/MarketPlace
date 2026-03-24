-- order_items 테이블에 외부몰 주문 상태 컬럼 추가
ALTER TABLE order_items
    ADD COLUMN external_order_status VARCHAR(50) NULL AFTER order_item_status;
