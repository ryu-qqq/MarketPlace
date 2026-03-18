CREATE TABLE external_order_item_mappings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sales_channel_id BIGINT NOT NULL,
    channel_code VARCHAR(20) NOT NULL,
    external_order_id VARCHAR(50) NOT NULL,
    external_product_order_id VARCHAR(50) NOT NULL,
    order_item_id VARCHAR(36) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    UNIQUE KEY uk_channel_ext_product_order (sales_channel_id, external_product_order_id),
    INDEX idx_order_item_id (order_item_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
