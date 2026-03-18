-- 레거시 주문 ID 매핑 테이블 (중복 방지)
CREATE TABLE legacy_order_id_mappings (
    id                      BIGINT          NOT NULL AUTO_INCREMENT,
    legacy_order_id         BIGINT          NOT NULL COMMENT '레거시 orders.order_id',
    legacy_payment_id       BIGINT          NOT NULL COMMENT '레거시 payment.payment_id',
    internal_order_id       VARCHAR(36)     NOT NULL COMMENT 'market.orders.id (UUID)',
    sales_channel_id        BIGINT          NOT NULL COMMENT '식별된 판매 채널 ID',
    channel_name            VARCHAR(50)     NOT NULL COMMENT '채널명 (NAVER, SSF, LF, OCO, SET_OF 등)',
    created_at              TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_legacy_order_id (legacy_order_id),
    INDEX idx_internal_order_id (internal_order_id),
    INDEX idx_sales_channel_id (sales_channel_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='레거시 주문 ID ↔ 내부 주문 ID 매핑';
