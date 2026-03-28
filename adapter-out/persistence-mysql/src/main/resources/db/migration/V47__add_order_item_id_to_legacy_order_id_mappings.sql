-- ============================================================
-- V47: legacy_order_id_mappings에 internal_order_item_id 컬럼 추가
-- ============================================================
-- 레거시 order 1개 = market orderItem 1개 매핑을 위해
-- orderItemId를 직접 저장하여 간접 조회 없이 바로 resolve.
-- ============================================================

ALTER TABLE legacy_order_id_mappings
    ADD COLUMN internal_order_item_id BIGINT NULL AFTER internal_order_id;

CREATE INDEX idx_legacy_order_mapping_order_item_id
    ON legacy_order_id_mappings (internal_order_item_id);
