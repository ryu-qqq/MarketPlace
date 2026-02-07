-- ============================================
-- Company Application Table
-- ============================================
-- 입점 신청 테이블
-- 상태: PENDING → APPROVED → COMPLETED
--       PENDING → REJECTED
-- ID: UUIDv7 (CHAR(36))
-- ============================================

CREATE TABLE company_application (
    id CHAR(36) NOT NULL COMMENT 'UUIDv7 기반 PK',

    -- 회사 정보
    business_number VARCHAR(10) NOT NULL COMMENT '사업자등록번호 (숫자 10자리)',
    company_name VARCHAR(100) NOT NULL COMMENT '상호명',
    representative_name VARCHAR(50) NOT NULL COMMENT '대표자명',
    ecommerce_registration VARCHAR(50) NOT NULL COMMENT '통신판매업 신고번호',

    -- 주소 정보
    zip_code VARCHAR(10) NOT NULL COMMENT '우편번호',
    address VARCHAR(255) NOT NULL COMMENT '기본 주소',
    address_detail VARCHAR(255) NULL COMMENT '상세 주소',

    -- 신청 담당자 정보
    applicant_name VARCHAR(50) NOT NULL COMMENT '담당자 이름',
    applicant_email VARCHAR(100) NOT NULL COMMENT '담당자 이메일',
    applicant_phone VARCHAR(20) NOT NULL COMMENT '담당자 연락처',

    -- 상태 관리
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '상태 (PENDING, APPROVED, REJECTED, COMPLETED)',

    -- 인증 토큰 (승인 시 생성)
    verification_token VARCHAR(64) NULL COMMENT '인증 토큰 (UUID)',
    token_expires_at TIMESTAMP(6) NULL COMMENT '토큰 만료 시간',

    -- 심사 정보
    reviewed_by VARCHAR(100) NULL COMMENT '심사자 이메일',
    reviewed_at TIMESTAMP(6) NULL COMMENT '심사 시간',
    rejection_reason VARCHAR(500) NULL COMMENT '거절 사유',

    -- 신청 시간
    applied_at TIMESTAMP(6) NOT NULL COMMENT '신청 시간',

    -- 낙관적 락
    version BIGINT NOT NULL DEFAULT 0 COMMENT '버전 (낙관적 락)',

    -- 감사 정보
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '생성 시간',
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '수정 시간',

    PRIMARY KEY (id),

    -- 인덱스
    INDEX idx_company_application_business_number (business_number),
    INDEX idx_company_application_status (status),
    INDEX idx_company_application_applied_at (applied_at),
    INDEX idx_company_application_verification_token (verification_token),
    INDEX idx_company_application_reviewed_by (reviewed_by)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='입점 신청';
