-- V83: 레거시 변환 Outbox 및 상품 ID 매핑 테이블 생성

CREATE TABLE IF NOT EXISTS legacy_conversion_outboxes (
    id                       BIGINT AUTO_INCREMENT PRIMARY KEY,
    legacy_product_group_id  BIGINT       NOT NULL COMMENT '레거시 상품그룹 ID (luxurydb)',
    status                   VARCHAR(20)  NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING, PROCESSING, COMPLETED, FAILED',
    retry_count              INT          NOT NULL DEFAULT 0,
    max_retry                INT          NOT NULL DEFAULT 3,
    created_at               DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at               DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    processed_at             DATETIME(6)  NULL,
    error_message            VARCHAR(1000) NULL,
    version                  BIGINT       NOT NULL DEFAULT 0,

    INDEX idx_legacy_conversion_outbox_status_created (status, created_at),
    INDEX idx_legacy_conversion_outbox_status_updated (status, updated_at),
    INDEX idx_legacy_conversion_outbox_legacy_group_id (legacy_product_group_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='레거시 상품 → 내부 상품 변환 Outbox';

CREATE TABLE IF NOT EXISTS legacy_product_id_mappings (
    id                        BIGINT AUTO_INCREMENT PRIMARY KEY,
    legacy_product_id         BIGINT      NOT NULL COMMENT '레거시 Product(SKU) ID (luxurydb)',
    internal_product_id       BIGINT      NOT NULL COMMENT '내부 Product ID',
    legacy_product_group_id   BIGINT      NOT NULL COMMENT '레거시 상품그룹 ID (그룹 참조)',
    internal_product_group_id BIGINT      NOT NULL COMMENT '내부 상품그룹 ID',
    created_at                DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

    UNIQUE INDEX uk_legacy_product_id_mapping_legacy (legacy_product_id),
    INDEX idx_legacy_product_id_mapping_internal (internal_product_id),
    INDEX idx_legacy_product_id_mapping_group (legacy_product_group_id),
    INDEX idx_legacy_product_id_mapping_internal_group (internal_product_group_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='레거시 ↔ 내부 Product(SKU) ID 매핑';
