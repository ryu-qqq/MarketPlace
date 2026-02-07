-- SalesChannelCategory, SalesChannelBrand 테이블 생성
-- 외부 판매 채널과 내부 시스템 간의 카테고리/브랜드 매핑 관리

-- sales_channel_category 테이블
CREATE TABLE sales_channel_category (
    id                      BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    sales_channel_id        BIGINT UNSIGNED NOT NULL COMMENT '판매 채널 ID (FK: sales_channel.id)',
    external_category_code  VARCHAR(100) NOT NULL COMMENT '외부 카테고리 코드',
    external_category_name  VARCHAR(500) NOT NULL COMMENT '외부 카테고리명 (경로 포함 가능)',
    internal_category_id    BIGINT UNSIGNED NULL COMMENT '내부 카테고리 ID (FK: category.id)',
    mapping_status          VARCHAR(20) NOT NULL DEFAULT 'UNMAPPED' COMMENT '매핑 상태 (MAPPED/UNMAPPED/PENDING)',
    mapped_at               DATETIME NULL COMMENT '매핑 시각',
    mapped_by               BIGINT UNSIGNED NULL COMMENT '매핑 담당자 ID',
    version                 BIGINT UNSIGNED NOT NULL DEFAULT 0,
    created_at              DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at              DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT uk_sales_channel_category_code UNIQUE (sales_channel_id, external_category_code),
    INDEX idx_scc_sales_channel_id (sales_channel_id),
    INDEX idx_scc_internal_category_id (internal_category_id),
    INDEX idx_scc_mapping_status (mapping_status),
    INDEX idx_scc_external_name (external_category_name(100)),
    INDEX idx_scc_updated (updated_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='판매 채널 카테고리 매핑 테이블';

-- sales_channel_brand 테이블
CREATE TABLE sales_channel_brand (
    id                      BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    sales_channel_id        BIGINT UNSIGNED NOT NULL COMMENT '판매 채널 ID (FK: sales_channel.id)',
    external_brand_code     VARCHAR(100) NOT NULL COMMENT '외부 브랜드 코드',
    external_brand_name     VARCHAR(200) NOT NULL COMMENT '외부 브랜드명',
    internal_brand_id       BIGINT UNSIGNED NULL COMMENT '내부 브랜드 ID (FK: brand.id)',
    mapping_status          VARCHAR(20) NOT NULL DEFAULT 'UNMAPPED' COMMENT '매핑 상태 (MAPPED/UNMAPPED/PENDING)',
    mapped_at               DATETIME NULL COMMENT '매핑 시각',
    mapped_by               BIGINT UNSIGNED NULL COMMENT '매핑 담당자 ID',
    version                 BIGINT UNSIGNED NOT NULL DEFAULT 0,
    created_at              DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at              DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT uk_sales_channel_brand_code UNIQUE (sales_channel_id, external_brand_code),
    INDEX idx_scb_sales_channel_id (sales_channel_id),
    INDEX idx_scb_internal_brand_id (internal_brand_id),
    INDEX idx_scb_mapping_status (mapping_status),
    INDEX idx_scb_external_name (external_brand_name),
    INDEX idx_scb_updated (updated_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='판매 채널 브랜드 매핑 테이블';
