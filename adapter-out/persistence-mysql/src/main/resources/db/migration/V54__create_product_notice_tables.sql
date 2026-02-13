-- ============================================
-- 상품 고시정보 테이블
-- product_notices, product_notice_entries
-- ============================================

-- ========================================
-- product_notices 테이블
-- ========================================

CREATE TABLE IF NOT EXISTS product_notices (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'PK',
    product_group_id BIGINT NOT NULL COMMENT '상품 그룹 ID',
    notice_category_id BIGINT NOT NULL COMMENT '고시정보 카테고리 ID',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    PRIMARY KEY (id),
    INDEX idx_product_notices_product_group_id (product_group_id),
    INDEX idx_product_notices_notice_category_id (notice_category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='상품 고시정보';

-- ========================================
-- product_notice_entries 테이블
-- ========================================

CREATE TABLE IF NOT EXISTS product_notice_entries (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'PK',
    product_notice_id BIGINT NOT NULL COMMENT '상품 고시정보 ID',
    notice_field_id BIGINT NOT NULL COMMENT '고시정보 항목 ID',
    field_value VARCHAR(500) NULL COMMENT '항목 값',
    PRIMARY KEY (id),
    INDEX idx_product_notice_entries_notice_id (product_notice_id),
    INDEX idx_product_notice_entries_field_id (notice_field_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='상품 고시정보 항목';
