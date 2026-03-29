-- =====================================================
-- V48: order_items에 category_id, regular_price 컬럼 추가
-- 레거시 주문 응답 호환을 위해 스냅샷 필드 보강
-- =====================================================

ALTER TABLE order_items
    ADD COLUMN category_id BIGINT NULL AFTER brand_id,
    ADD COLUMN regular_price INT NOT NULL DEFAULT 0 AFTER external_image_url;
