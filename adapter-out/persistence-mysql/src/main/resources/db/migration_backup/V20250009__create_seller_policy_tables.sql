-- Wave 2: Seller and Policy Tables
-- 셀러 관리 및 배송/반품/환불 정책 테이블

-- ========================================
-- Seller 테이블
-- ========================================

CREATE TABLE IF NOT EXISTS seller (
    id BINARY(16) NOT NULL COMMENT 'UUID',
    company_id BIGINT NOT NULL COMMENT '회사(입점사) ID',
    organization_id VARCHAR(100) NOT NULL COMMENT '조직 ID (외부 식별자)',
    name VARCHAR(200) NOT NULL COMMENT '셀러명',
    description TEXT NULL COMMENT '셀러 설명',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '상태 (ACTIVE, INACTIVE, SUSPENDED)',
    version BIGINT NOT NULL DEFAULT 0 COMMENT '낙관적 잠금 버전',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    PRIMARY KEY (id),
    UNIQUE KEY uk_seller_organization_id (organization_id),
    INDEX idx_seller_company_id (company_id),
    INDEX idx_seller_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='셀러 정보';

-- ========================================
-- Seller Sales Channel 연결 테이블
-- ========================================

CREATE TABLE IF NOT EXISTS seller_sales_channel (
    id BINARY(16) NOT NULL COMMENT 'UUID',
    seller_id BINARY(16) NOT NULL COMMENT '셀러 ID (FK)',
    sales_channel_id BIGINT NOT NULL COMMENT '판매 채널 ID',
    channel_code VARCHAR(50) NOT NULL COMMENT '채널 코드 (ZIGZAG, ABLY 등)',
    connection_status VARCHAR(30) NOT NULL DEFAULT 'DISCONNECTED' COMMENT '연결 상태',
    api_key VARCHAR(500) NULL COMMENT 'API Key',
    api_secret VARCHAR(500) NULL COMMENT 'API Secret',
    access_token VARCHAR(2000) NULL COMMENT 'Access Token',
    vendor_id VARCHAR(100) NULL COMMENT '벤더 ID',
    display_name VARCHAR(200) NULL COMMENT '표시명',
    version BIGINT NOT NULL DEFAULT 0 COMMENT '낙관적 잠금 버전',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    PRIMARY KEY (id),
    UNIQUE KEY uk_seller_sales_channel (seller_id, sales_channel_id),
    INDEX idx_seller_sales_channel_seller (seller_id),
    INDEX idx_seller_sales_channel_channel (sales_channel_id),
    INDEX idx_seller_sales_channel_status (connection_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='셀러-판매채널 연결';

-- ========================================
-- Shipping Policy 테이블
-- ========================================

CREATE TABLE IF NOT EXISTS shipping_policy (
    id VARCHAR(36) NOT NULL COMMENT 'UUIDv7',
    seller_id BIGINT NOT NULL COMMENT '셀러 ID',
    policy_name VARCHAR(100) NOT NULL COMMENT '정책명',
    shipping_fee DECIMAL(15,2) NOT NULL COMMENT '배송비',
    free_shipping_min_amount DECIMAL(15,2) NULL COMMENT '무료배송 최소 금액',
    is_default TINYINT(1) NOT NULL DEFAULT 0 COMMENT '기본 정책 여부',
    deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '삭제 여부 (soft delete)',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    PRIMARY KEY (id),
    INDEX idx_shipping_policy_seller (seller_id),
    INDEX idx_shipping_policy_default (seller_id, is_default),
    INDEX idx_shipping_policy_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='배송 정책';

-- ========================================
-- Shipping Warehouse 테이블 (출고지)
-- ========================================

CREATE TABLE IF NOT EXISTS shipping_warehouse (
    id VARCHAR(36) NOT NULL COMMENT 'UUIDv7',
    shipping_policy_id VARCHAR(36) NOT NULL COMMENT '배송 정책 ID (FK)',
    name VARCHAR(100) NOT NULL COMMENT '출고지명',
    zip_code VARCHAR(10) NOT NULL COMMENT '우편번호',
    base_address VARCHAR(200) NOT NULL COMMENT '기본주소',
    detail_address VARCHAR(100) NULL COMMENT '상세주소',
    phone VARCHAR(20) NOT NULL COMMENT '연락처',
    is_default TINYINT(1) NOT NULL DEFAULT 0 COMMENT '기본 출고지 여부',
    PRIMARY KEY (id),
    INDEX idx_shipping_warehouse_policy (shipping_policy_id),
    INDEX idx_shipping_warehouse_default (shipping_policy_id, is_default)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='출고지(창고)';

-- ========================================
-- Return Policy 테이블
-- ========================================

CREATE TABLE IF NOT EXISTS return_policy (
    id VARCHAR(36) NOT NULL COMMENT 'UUIDv7',
    seller_id BIGINT NOT NULL COMMENT '셀러 ID',
    policy_name VARCHAR(100) NOT NULL COMMENT '정책명',
    return_days INT NOT NULL COMMENT '반품 가능 기간 (일)',
    return_fee DECIMAL(19,2) NOT NULL COMMENT '반품 배송비',
    conditions VARCHAR(2000) NULL COMMENT '반품 조건',
    is_default TINYINT(1) NOT NULL DEFAULT 0 COMMENT '기본 정책 여부',
    deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '삭제 여부 (soft delete)',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    PRIMARY KEY (id),
    INDEX idx_return_policy_seller (seller_id),
    INDEX idx_return_policy_default (seller_id, is_default),
    INDEX idx_return_policy_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='반품 정책';

-- ========================================
-- Return Address 테이블 (반품지)
-- ========================================

CREATE TABLE IF NOT EXISTS return_address (
    id VARCHAR(36) NOT NULL COMMENT 'UUIDv7',
    return_policy_id VARCHAR(36) NOT NULL COMMENT '반품 정책 ID (FK)',
    name VARCHAR(100) NOT NULL COMMENT '반품지명',
    zip_code VARCHAR(5) NOT NULL COMMENT '우편번호',
    base_address VARCHAR(200) NOT NULL COMMENT '기본주소',
    detail_address VARCHAR(100) NULL COMMENT '상세주소',
    phone VARCHAR(20) NOT NULL COMMENT '연락처',
    is_default TINYINT(1) NOT NULL DEFAULT 0 COMMENT '기본 반품지 여부',
    PRIMARY KEY (id),
    INDEX idx_return_address_policy (return_policy_id),
    INDEX idx_return_address_default (return_policy_id, is_default)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='반품지';

-- ========================================
-- Refund Policy 테이블
-- ========================================

CREATE TABLE IF NOT EXISTS refund_policy (
    id VARCHAR(36) NOT NULL COMMENT 'UUIDv7',
    seller_id BIGINT NOT NULL COMMENT '셀러 ID',
    policy_name VARCHAR(100) NOT NULL COMMENT '정책명',
    refund_days INT NOT NULL COMMENT '환불 처리 기간 (일)',
    refund_method VARCHAR(50) NOT NULL COMMENT '환불 방법 (ORIGINAL_PAYMENT, BANK_TRANSFER, POINT, COUPON)',
    conditions VARCHAR(2000) NULL COMMENT '환불 조건',
    is_default TINYINT(1) NOT NULL DEFAULT 0 COMMENT '기본 정책 여부',
    deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '삭제 여부 (soft delete)',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    PRIMARY KEY (id),
    INDEX idx_refund_policy_seller (seller_id),
    INDEX idx_refund_policy_default (seller_id, is_default),
    INDEX idx_refund_policy_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='환불 정책';
