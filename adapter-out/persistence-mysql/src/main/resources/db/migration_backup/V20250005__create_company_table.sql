-- Company 테이블 생성
-- 입점이 완료된 회사(사업체) 정보

CREATE TABLE IF NOT EXISTS company (
    id VARCHAR(36) NOT NULL,
    tenant_id VARCHAR(36) NULL,
    application_id VARCHAR(36) NOT NULL,
    business_number VARCHAR(20) NOT NULL,
    company_name VARCHAR(200) NOT NULL,
    representative_name VARCHAR(100) NOT NULL,
    ecommerce_registration_number VARCHAR(100) NOT NULL,
    zip_code VARCHAR(10) NOT NULL,
    base_address VARCHAR(500) NOT NULL,
    detail_address VARCHAR(500) NULL,
    bank_code VARCHAR(10) NULL,
    bank_name VARCHAR(50) NULL,
    account_holder VARCHAR(100) NULL,
    account_number VARCHAR(50) NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    registered_at TIMESTAMP NOT NULL,
    last_active_at TIMESTAMP NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_company_business_number (business_number),
    UNIQUE KEY uk_company_application_id (application_id),
    INDEX idx_company_status (status),
    INDEX idx_company_tenant_id (tenant_id),
    INDEX idx_company_registered_at (registered_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='회사(입점사) 정보';
