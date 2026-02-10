CREATE TABLE sales_channel_brand (
    id                    BIGINT AUTO_INCREMENT PRIMARY KEY,
    sales_channel_id      BIGINT       NOT NULL COMMENT '판매채널 ID',
    external_brand_code   VARCHAR(200) NOT NULL COMMENT '외부 브랜드 코드',
    external_brand_name   VARCHAR(500) NOT NULL COMMENT '외부 브랜드명',
    status                VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    created_at            DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at            DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    UNIQUE KEY uq_scb_sc_code (sales_channel_id, external_brand_code),
    INDEX idx_scb_sc (sales_channel_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='외부 채널 브랜드 테이블';
