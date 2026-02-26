-- ============================================
-- 브랜드 마스터 테이블
-- ============================================

CREATE TABLE IF NOT EXISTS brand (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'PK',
    code VARCHAR(100) NOT NULL COMMENT '브랜드 코드',
    name_ko VARCHAR(255) NULL COMMENT '브랜드 한글명',
    name_en VARCHAR(255) NULL COMMENT '브랜드 영문명',
    short_name VARCHAR(100) NULL COMMENT '브랜드 약칭',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '상태 (ACTIVE, INACTIVE 등)',
    logo_url VARCHAR(500) NULL COMMENT '로고 URL',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    deleted_at TIMESTAMP NULL DEFAULT NULL COMMENT '삭제일시 (soft delete)',
    PRIMARY KEY (id),
    UNIQUE KEY uk_brand_code (code),
    KEY idx_brand_status (status),
    KEY idx_brand_updated (updated_at),
    KEY idx_brand_deleted (deleted_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='표준 브랜드 마스터 테이블';
