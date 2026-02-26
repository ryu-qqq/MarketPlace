-- ============================================
-- 셀러 관리자 테이블
-- ID: UUIDv7 String (외부 생성)
-- ============================================

CREATE TABLE IF NOT EXISTS seller_admins (
    id VARCHAR(36) NOT NULL COMMENT 'UUIDv7 PK',
    seller_id BIGINT NOT NULL COMMENT '셀러 ID',
    auth_user_id VARCHAR(100) NULL COMMENT '인증 서버 사용자 ID',
    login_id VARCHAR(100) NOT NULL COMMENT '로그인 ID',
    name VARCHAR(50) NOT NULL COMMENT '관리자명',
    phone_number VARCHAR(20) NULL COMMENT '연락처',
    status VARCHAR(30) NOT NULL COMMENT '상태 (PENDING_APPROVAL, ACTIVE, SUSPENDED, DEACTIVATED)',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    deleted_at TIMESTAMP NULL DEFAULT NULL COMMENT '삭제일시 (soft delete)',
    PRIMARY KEY (id),
    UNIQUE KEY uk_seller_admins_login_id (login_id),
    INDEX idx_seller_admins_seller_id (seller_id),
    INDEX idx_seller_admins_status (status),
    INDEX idx_seller_admins_deleted (deleted_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='셀러 관리자';
