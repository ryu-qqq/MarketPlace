-- V63: 외부 데이터 소스 인바운드 매핑 테이블
-- 외부 소스(크롤링, 레거시, 파트너)에서 유입되는 상품 데이터를 내부 구조로 매핑

-- 1. external_source: 외부 데이터 소스 관리
CREATE TABLE external_source (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    code        VARCHAR(100)  NOT NULL COMMENT '외부 소스 고유 코드 (e.g. SETOF, COUPANG_CRAWL)',
    name        VARCHAR(200)  NOT NULL COMMENT '외부 소스 표시명',
    type        VARCHAR(30)   NOT NULL COMMENT 'CRAWLING, LEGACY, PARTNER',
    status      VARCHAR(20)   NOT NULL DEFAULT 'ACTIVE' COMMENT 'ACTIVE, INACTIVE',
    description VARCHAR(1000) NULL     COMMENT '설명',
    created_at  DATETIME(6)   NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '생성일시',
    updated_at  DATETIME(6)   NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '수정일시',
    UNIQUE KEY uq_es_code (code),
    INDEX idx_es_type (type),
    INDEX idx_es_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='외부 데이터 소스 (크롤링, 레거시, 파트너)';

-- 2. external_brand_mapping: 외부 소스별 브랜드 매핑
CREATE TABLE external_brand_mapping (
    id                    BIGINT       AUTO_INCREMENT PRIMARY KEY,
    external_source_id    BIGINT       NOT NULL COMMENT '외부 소스 ID',
    external_brand_code   VARCHAR(200) NOT NULL COMMENT '외부 소스의 브랜드 코드',
    external_brand_name   VARCHAR(500) NULL     COMMENT '외부 소스의 브랜드명',
    internal_brand_id     BIGINT       NOT NULL COMMENT '내부 Brand ID',
    status                VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE' COMMENT 'ACTIVE, INACTIVE',
    created_at            DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '생성일시',
    updated_at            DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '수정일시',
    UNIQUE KEY uq_ebm_source_code (external_source_id, external_brand_code),
    INDEX idx_ebm_internal (internal_brand_id),
    INDEX idx_ebm_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='외부 소스 → 내부 브랜드 매핑';

-- 3. external_category_mapping: 외부 소스별 카테고리 매핑
CREATE TABLE external_category_mapping (
    id                       BIGINT       AUTO_INCREMENT PRIMARY KEY,
    external_source_id       BIGINT       NOT NULL COMMENT '외부 소스 ID',
    external_category_code   VARCHAR(200) NOT NULL COMMENT '외부 소스의 카테고리 코드',
    external_category_name   VARCHAR(500) NULL     COMMENT '외부 소스의 카테고리명',
    internal_category_id     BIGINT       NOT NULL COMMENT '내부 Category ID',
    status                   VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE' COMMENT 'ACTIVE, INACTIVE',
    created_at               DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '생성일시',
    updated_at               DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '수정일시',
    UNIQUE KEY uq_ecm_source_code (external_source_id, external_category_code),
    INDEX idx_ecm_internal (internal_category_id),
    INDEX idx_ecm_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='외부 소스 → 내부 카테고리 매핑';
