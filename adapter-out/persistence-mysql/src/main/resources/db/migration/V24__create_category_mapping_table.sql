CREATE TABLE category_mapping (
    id                          BIGINT AUTO_INCREMENT PRIMARY KEY,
    sales_channel_category_id   BIGINT       NOT NULL COMMENT '외부 채널 카테고리 ID',
    internal_category_id        BIGINT       NOT NULL COMMENT '내부 카테고리 ID',
    status                      VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    created_at                  DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at                  DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    UNIQUE KEY uq_cm_sc_category (sales_channel_category_id),
    INDEX idx_cm_internal (internal_category_id),
    INDEX idx_cm_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='외부↔내부 카테고리 매핑 테이블';
