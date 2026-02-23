CREATE TABLE intelligence_outboxes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_group_id BIGINT NOT NULL,
    profile_id BIGINT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    retry_count INT NOT NULL DEFAULT 0,
    max_retry INT NOT NULL DEFAULT 3,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    processed_at DATETIME(6) NULL,
    error_message VARCHAR(1000) NULL,
    version BIGINT NOT NULL DEFAULT 0,
    idempotency_key VARCHAR(100) NOT NULL,
    UNIQUE INDEX uk_io_idempotency (idempotency_key),
    INDEX idx_io_status_created (status, created_at),
    INDEX idx_io_status_updated (status, updated_at),
    INDEX idx_io_product_group (product_group_id)
);
