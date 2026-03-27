-- legacy_order_conversion_outboxes: UNIQUE KEY → 일반 INDEX 변경
-- 레거시 주문 상태 변경 시 동일 주문에 대해 재처리 PENDING INSERT 허용
ALTER TABLE legacy_order_conversion_outboxes
    DROP INDEX uk_legacy_order_id,
    ADD INDEX idx_legacy_order_id (legacy_order_id);
