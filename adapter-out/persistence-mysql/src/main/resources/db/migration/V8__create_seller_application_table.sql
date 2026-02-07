-- ============================================
-- 입점 신청 테이블
-- BaseAuditEntity (soft delete 미지원)
-- ============================================

CREATE TABLE IF NOT EXISTS seller_applications (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'PK',

    -- 신청자 기본 정보
    seller_name VARCHAR(100) NOT NULL COMMENT '셀러명',
    display_name VARCHAR(100) NOT NULL COMMENT '표시명',
    logo_url VARCHAR(500) NULL COMMENT '로고 URL',
    description VARCHAR(2000) NULL COMMENT '셀러 설명',

    -- 사업자 정보
    registration_number VARCHAR(20) NOT NULL COMMENT '사업자등록번호',
    company_name VARCHAR(100) NOT NULL COMMENT '회사명',
    representative VARCHAR(50) NOT NULL COMMENT '대표자명',
    sale_report_number VARCHAR(50) NULL COMMENT '통신판매업신고번호',
    business_zip_code VARCHAR(10) NOT NULL COMMENT '사업장 우편번호',
    business_base_address VARCHAR(200) NOT NULL COMMENT '사업장 기본주소',
    business_detail_address VARCHAR(200) NULL COMMENT '사업장 상세주소',

    -- CS 정보
    cs_phone_number VARCHAR(20) NOT NULL COMMENT 'CS 전화번호',
    cs_email VARCHAR(100) NOT NULL COMMENT 'CS 이메일',

    -- 정산 정보
    bank_code VARCHAR(10) NULL COMMENT '은행 코드',
    bank_name VARCHAR(50) NOT NULL COMMENT '은행명',
    account_number VARCHAR(30) NOT NULL COMMENT '계좌번호',
    account_holder_name VARCHAR(50) NOT NULL COMMENT '예금주명',
    settlement_cycle VARCHAR(20) NOT NULL COMMENT '정산 주기 (WEEKLY, BIWEEKLY, MONTHLY)',
    settlement_day INT NOT NULL COMMENT '정산일',

    -- 동의 정보
    agreed_at TIMESTAMP NOT NULL COMMENT '약관 동의 일시',

    -- 상태 관리
    status VARCHAR(20) NOT NULL COMMENT '신청 상태 (PENDING, APPROVED, REJECTED)',
    applied_at TIMESTAMP NOT NULL COMMENT '신청일시',
    processed_at TIMESTAMP NULL DEFAULT NULL COMMENT '처리일시',
    processed_by VARCHAR(100) NULL COMMENT '처리자',
    rejection_reason VARCHAR(500) NULL COMMENT '거절 사유',

    -- 승인 후 생성된 셀러 ID
    approved_seller_id BIGINT NULL COMMENT '승인된 셀러 ID',

    -- 감사 필드
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',

    PRIMARY KEY (id),
    INDEX idx_seller_applications_status (status),
    INDEX idx_seller_applications_reg_num (registration_number),
    INDEX idx_seller_applications_applied_at (applied_at),
    INDEX idx_seller_applications_status_reg (status, registration_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='입점 신청';
