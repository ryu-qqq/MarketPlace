-- 주문 상품에 seller_id, brand_id 컬럼 추가 (클레임 처리 시 셀러별 필터링 필요)
ALTER TABLE order_items
    ADD COLUMN seller_id BIGINT NULL AFTER product_group_id,
    ADD COLUMN brand_id BIGINT NULL AFTER seller_id;
