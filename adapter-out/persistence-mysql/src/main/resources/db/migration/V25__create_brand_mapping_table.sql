CREATE TABLE brand_mapping (
    id                        BIGINT AUTO_INCREMENT PRIMARY KEY,
    sales_channel_brand_id    BIGINT       NOT NULL COMMENT '외부 채널 브랜드 ID',
    internal_brand_id         BIGINT       NOT NULL COMMENT '내부 브랜드 ID',
    status                    VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    created_at                DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at                DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    UNIQUE KEY uq_bm_sc_brand (sales_channel_brand_id),
    INDEX idx_bm_internal (internal_brand_id),
    INDEX idx_bm_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='외부↔내부 브랜드 매핑 테이블';
