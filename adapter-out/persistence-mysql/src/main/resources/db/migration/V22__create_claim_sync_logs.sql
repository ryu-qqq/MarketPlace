CREATE TABLE claim_sync_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sales_channel_id BIGINT NOT NULL,
    external_product_order_id VARCHAR(50) NOT NULL,
    external_claim_type VARCHAR(30) NOT NULL,
    external_claim_status VARCHAR(50) NOT NULL,
    internal_claim_type VARCHAR(20) NOT NULL,
    internal_claim_id BIGINT NOT NULL,
    action VARCHAR(30) NOT NULL,
    synced_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    UNIQUE KEY uk_sync_key (sales_channel_id, external_product_order_id, external_claim_type, external_claim_status),
    INDEX idx_internal_claim (internal_claim_type, internal_claim_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
