-- ============================================
-- 셀러 관련 테이블
-- sellers, seller_business_infos, seller_cs,
-- seller_contracts, seller_settlements
-- ============================================

-- ========================================
-- sellers 테이블
-- ========================================

CREATE TABLE IF NOT EXISTS sellers (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'PK',
    seller_name VARCHAR(100) NOT NULL COMMENT '셀러명',
    display_name VARCHAR(100) NOT NULL COMMENT '표시명',
    logo_url VARCHAR(500) NULL COMMENT '로고 URL',
    description VARCHAR(2000) NULL COMMENT '셀러 설명',
    is_active TINYINT(1) NOT NULL DEFAULT 1 COMMENT '활성 여부',
    auth_tenant_id VARCHAR(100) NULL COMMENT '인증 테넌트 ID',
    auth_organization_id VARCHAR(100) NULL COMMENT '인증 조직 ID',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    deleted_at TIMESTAMP NULL DEFAULT NULL COMMENT '삭제일시 (soft delete)',
    PRIMARY KEY (id),
    UNIQUE KEY uk_sellers_seller_name (seller_name),
    INDEX idx_sellers_active (is_active),
    INDEX idx_sellers_deleted (deleted_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='셀러';

-- ========================================
-- seller_business_infos 테이블
-- ========================================

CREATE TABLE IF NOT EXISTS seller_business_infos (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'PK',
    seller_id BIGINT NOT NULL COMMENT '셀러 ID',
    registration_number VARCHAR(20) NOT NULL COMMENT '사업자등록번호',
    company_name VARCHAR(100) NOT NULL COMMENT '회사명',
    representative VARCHAR(50) NOT NULL COMMENT '대표자명',
    sale_report_number VARCHAR(50) NULL COMMENT '통신판매업신고번호',
    business_zipcode VARCHAR(10) NULL COMMENT '사업장 우편번호',
    business_address VARCHAR(200) NULL COMMENT '사업장 기본주소',
    business_address_detail VARCHAR(200) NULL COMMENT '사업장 상세주소',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    deleted_at TIMESTAMP NULL DEFAULT NULL COMMENT '삭제일시 (soft delete)',
    PRIMARY KEY (id),
    UNIQUE KEY uk_seller_business_infos_reg_num (registration_number),
    INDEX idx_seller_business_infos_seller_id (seller_id),
    INDEX idx_seller_business_infos_deleted (deleted_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='셀러 사업자 정보';

-- ========================================
-- seller_cs 테이블
-- ========================================

CREATE TABLE IF NOT EXISTS seller_cs (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'PK',
    seller_id BIGINT NOT NULL COMMENT '셀러 ID',
    cs_phone VARCHAR(20) NOT NULL COMMENT 'CS 전화번호',
    cs_mobile VARCHAR(20) NULL COMMENT 'CS 휴대폰번호',
    cs_email VARCHAR(100) NOT NULL COMMENT 'CS 이메일',
    operating_start_time TIME NULL COMMENT '운영 시작 시간',
    operating_end_time TIME NULL COMMENT '운영 종료 시간',
    operating_days VARCHAR(50) NULL COMMENT '운영 요일 (MON,TUE,...)',
    kakao_channel_url VARCHAR(500) NULL COMMENT '카카오 채널 URL',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    deleted_at TIMESTAMP NULL DEFAULT NULL COMMENT '삭제일시 (soft delete)',
    PRIMARY KEY (id),
    INDEX idx_seller_cs_seller_id (seller_id),
    INDEX idx_seller_cs_deleted (deleted_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='셀러 CS 정보';

-- ========================================
-- seller_contracts 테이블
-- ========================================

CREATE TABLE IF NOT EXISTS seller_contracts (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'PK',
    seller_id BIGINT NOT NULL COMMENT '셀러 ID',
    commission_rate DECIMAL(5,2) NOT NULL COMMENT '수수료율 (%)',
    contract_start_date DATE NOT NULL COMMENT '계약 시작일',
    contract_end_date DATE NULL COMMENT '계약 종료일',
    status VARCHAR(20) NOT NULL COMMENT '계약 상태 (ACTIVE, EXPIRED, TERMINATED)',
    special_terms TEXT NULL COMMENT '특약 사항',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    deleted_at TIMESTAMP NULL DEFAULT NULL COMMENT '삭제일시 (soft delete)',
    PRIMARY KEY (id),
    INDEX idx_seller_contracts_seller_id (seller_id),
    INDEX idx_seller_contracts_status (status),
    INDEX idx_seller_contracts_deleted (deleted_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='셀러 계약 정보';

-- ========================================
-- seller_settlements 테이블
-- ========================================

CREATE TABLE IF NOT EXISTS seller_settlements (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'PK',
    seller_id BIGINT NOT NULL COMMENT '셀러 ID',
    bank_code VARCHAR(10) NULL COMMENT '은행 코드',
    bank_name VARCHAR(50) NULL COMMENT '은행명',
    account_number VARCHAR(30) NULL COMMENT '계좌번호',
    account_holder_name VARCHAR(50) NULL COMMENT '예금주명',
    settlement_cycle VARCHAR(20) NOT NULL COMMENT '정산 주기 (WEEKLY, BIWEEKLY, MONTHLY)',
    settlement_day INT NOT NULL COMMENT '정산일',
    is_verified TINYINT(1) NOT NULL DEFAULT 0 COMMENT '계좌 인증 여부',
    verified_at TIMESTAMP NULL DEFAULT NULL COMMENT '계좌 인증일시',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    deleted_at TIMESTAMP NULL DEFAULT NULL COMMENT '삭제일시 (soft delete)',
    PRIMARY KEY (id),
    INDEX idx_seller_settlements_seller_id (seller_id),
    INDEX idx_seller_settlements_deleted (deleted_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='셀러 정산 정보';
