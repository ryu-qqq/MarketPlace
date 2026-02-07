-- ============================================
-- 공통 코드 타입 & 공통 코드 테이블
-- ============================================

-- ========================================
-- common_code_types 테이블
-- ========================================

CREATE TABLE IF NOT EXISTS common_code_types (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'PK',
    code VARCHAR(50) NOT NULL COMMENT '코드 타입 식별자',
    name VARCHAR(100) NOT NULL COMMENT '코드 타입명',
    description VARCHAR(500) NULL COMMENT '설명',
    display_order INT NOT NULL COMMENT '표시 순서',
    is_active TINYINT(1) NOT NULL DEFAULT 1 COMMENT '활성 여부',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    deleted_at TIMESTAMP NULL DEFAULT NULL COMMENT '삭제일시 (soft delete)',
    PRIMARY KEY (id),
    UNIQUE KEY uk_common_code_types_code (code),
    INDEX idx_common_code_types_active (is_active),
    INDEX idx_common_code_types_deleted (deleted_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='공통 코드 타입';

-- ========================================
-- common_codes 테이블
-- ========================================

CREATE TABLE IF NOT EXISTS common_codes (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'PK',
    common_code_type_id BIGINT NOT NULL COMMENT '코드 타입 ID',
    code VARCHAR(50) NOT NULL COMMENT '코드 값',
    display_name VARCHAR(100) NOT NULL COMMENT '표시명',
    display_order INT NOT NULL COMMENT '표시 순서',
    is_active TINYINT(1) NOT NULL DEFAULT 1 COMMENT '활성 여부',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    deleted_at TIMESTAMP NULL DEFAULT NULL COMMENT '삭제일시 (soft delete)',
    PRIMARY KEY (id),
    INDEX idx_common_codes_type_id (common_code_type_id),
    INDEX idx_common_codes_active (is_active),
    INDEX idx_common_codes_deleted (deleted_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='공통 코드';
