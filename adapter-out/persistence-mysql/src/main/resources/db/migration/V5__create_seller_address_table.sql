-- ============================================
-- 셀러 주소 테이블 (독립 Aggregate)
-- 출고지/반품지 등 1:N 관계
-- ============================================

CREATE TABLE IF NOT EXISTS seller_addresses (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'PK',
    seller_id BIGINT NOT NULL COMMENT '셀러 ID',
    address_type VARCHAR(20) NOT NULL COMMENT '주소 유형 (SHIPPING, RETURN 등)',
    address_name VARCHAR(50) NOT NULL COMMENT '주소명 (예: 본사 출고지)',
    zipcode VARCHAR(10) NULL COMMENT '우편번호',
    address VARCHAR(200) NULL COMMENT '기본주소',
    address_detail VARCHAR(200) NULL COMMENT '상세주소',
    contact_name VARCHAR(50) NULL COMMENT '담당자명',
    contact_phone VARCHAR(20) NULL COMMENT '담당자 연락처',
    is_default TINYINT(1) NOT NULL DEFAULT 0 COMMENT '기본 주소 여부',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    deleted_at TIMESTAMP NULL DEFAULT NULL COMMENT '삭제일시 (soft delete)',
    PRIMARY KEY (id),
    INDEX idx_seller_addresses_seller_id (seller_id),
    INDEX idx_seller_addresses_type (seller_id, address_type),
    INDEX idx_seller_addresses_default (seller_id, is_default),
    INDEX idx_seller_addresses_deleted (deleted_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='셀러 주소 (출고지/반품지)';
