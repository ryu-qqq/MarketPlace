-- 레거시 주문 이관 Outbox 테이블
CREATE TABLE legacy_order_conversion_outboxes (
    id                      BIGINT          NOT NULL AUTO_INCREMENT,
    legacy_order_id         BIGINT          NOT NULL COMMENT '레거시 orders.order_id',
    legacy_payment_id       BIGINT          NOT NULL COMMENT '레거시 payment.payment_id',
    status                  VARCHAR(20)     NOT NULL DEFAULT 'PENDING',
    retry_count             INT             NOT NULL DEFAULT 0,
    max_retry               INT             NOT NULL DEFAULT 3,
    error_message           VARCHAR(2000)   NULL,
    processed_at            TIMESTAMP       NULL,
    created_at              TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at              TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version                 BIGINT          NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uk_legacy_order_id (legacy_order_id),
    INDEX idx_status_updated (status, updated_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='레거시 주문 이관 Outbox';
