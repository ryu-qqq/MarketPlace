-- ================================================
-- Product Table Migration
-- 상품 테이블 생성
-- ================================================

CREATE TABLE product (
    -- Primary Key
    id                  BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,

    -- 고유 식별자
    product_code        VARCHAR(100) NOT NULL COMMENT '상품 고유 코드 (예: PROD001)',

    -- 이름 정보
    name_korean         VARCHAR(500) NOT NULL COMMENT '한글 상품명',
    name_english        VARCHAR(500) COMMENT '영문 상품명',

    -- 상태 정보
    status              VARCHAR(20) NOT NULL DEFAULT 'DRAFT' COMMENT '상품 상태 (DRAFT, ACTIVE, INACTIVE, SOLD_OUT, DELETED)',

    -- FK 참조 (Long FK 전략)
    category_id         BIGINT UNSIGNED NOT NULL COMMENT '카테고리 ID',
    brand_id            BIGINT UNSIGNED NOT NULL COMMENT '브랜드 ID',
    seller_id           VARCHAR(36) NOT NULL COMMENT '판매자 ID (UUID)',
    shipping_policy_id  VARCHAR(36) NOT NULL COMMENT '배송정책 ID (UUIDv7)',

    -- 가격 정보
    base_price          BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '정상가 (원)',
    sale_price          BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '판매가 (원)',

    -- 상품 설명
    short_description   VARCHAR(2000) COMMENT '간단 설명',
    detail_description  TEXT COMMENT '상세 설명 (HTML)',

    -- 낙관적 락 & 감사 필드
    version             BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '낙관적 락 버전',
    created_at          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 일시',
    updated_at          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 일시',

    -- 제약 조건
    UNIQUE KEY uk_product_code (product_code),

    -- 인덱스
    KEY idx_product_seller (seller_id),
    KEY idx_product_category (category_id),
    KEY idx_product_brand (brand_id),
    KEY idx_product_status (status),
    KEY idx_product_created (created_at),
    KEY idx_product_updated (updated_at)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='상품 테이블';
