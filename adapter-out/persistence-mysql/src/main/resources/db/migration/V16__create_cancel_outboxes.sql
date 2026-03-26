-- 취소 아웃박스 테이블: 외부 채널 동기화를 위한 Outbox 패턴
CREATE TABLE IF NOT EXISTS cancel_outboxes (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    order_item_id   VARCHAR(36)     NOT NULL COMMENT '주문상품 ID (UUIDv7)',
    outbox_type     VARCHAR(20)     NOT NULL COMMENT 'SELLER_CANCEL, APPROVE, REJECT',
    status          VARCHAR(20)     NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING, PROCESSING, COMPLETED, FAILED',
    payload         TEXT            NULL COMMENT 'JSON 페이로드',
    retry_count     INT             NOT NULL DEFAULT 0,
    max_retry       INT             NOT NULL DEFAULT 3,
    created_at      DATETIME(6)     NOT NULL,
    updated_at      DATETIME(6)     NULL ON UPDATE CURRENT_TIMESTAMP(6),
    processed_at    DATETIME(6)     NULL,
    error_message   VARCHAR(1000)   NULL,
    version         BIGINT          NOT NULL DEFAULT 0 COMMENT '낙관적 락',
    idempotency_key VARCHAR(100)    NOT NULL COMMENT '멱등키',
    PRIMARY KEY (id),
    UNIQUE KEY uk_cancel_outboxes_idempotency (idempotency_key),
    KEY idx_cancel_outboxes_status_retry (status, retry_count),
    KEY idx_cancel_outboxes_order_item_id (order_item_id),
    KEY idx_cancel_outboxes_status_created (status, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
