CREATE TABLE outbound_products (
    id                    BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_group_id      BIGINT NOT NULL,
    sales_channel_id      BIGINT NOT NULL,
    external_product_id   VARCHAR(255),
    status                VARCHAR(50) NOT NULL,
    created_at            DATETIME(6) NOT NULL,
    updated_at            DATETIME(6) NOT NULL,
    UNIQUE KEY uk_pg_channel (product_group_id, sales_channel_id),
    INDEX idx_status (status),
    INDEX idx_external_product (external_product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
