-- ================================================
-- V20250028: 사이즈 환산 테이블 생성
-- 국제 사이즈 체계 간 환산 규칙 관리
-- ================================================

-- -----------------------------------------------------
-- 1. size_conversion 테이블 생성
-- 사이즈 간 환산 관계 정의 (EU 42 = US 9 = UK 8 등)
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS size_conversion (
    -- Primary Key
    id                      BIGINT AUTO_INCREMENT PRIMARY KEY,

    -- FK (Long FK 전략)
    from_option_dictionary_id   BIGINT NOT NULL COMMENT '원본 옵션 사전 ID (예: EU 42)',
    to_option_dictionary_id     BIGINT NOT NULL COMMENT '대상 옵션 사전 ID (예: US 9)',

    -- 카테고리 분류
    category_type           VARCHAR(50) NOT NULL COMMENT '카테고리 타입 (SHOES, CLOTHING_TOP, CLOTHING_BOTTOM, RING 등)',

    -- 환산 신뢰도 및 브랜드 특화
    confidence              DECIMAL(3, 2) NOT NULL DEFAULT 1.00 COMMENT '환산 신뢰도 (0.00~1.00, 1.00 = 표준 환산)',
    brand_id                BIGINT NULL COMMENT '브랜드 ID (NULL = 일반 환산, 값 = 브랜드 특화 환산)',

    -- 설명
    description             VARCHAR(500) COMMENT '환산 설명 (예: 럭셔리 브랜드는 보통 한 치수 작음)',

    -- 낙관적 락 & 감사 필드
    version                 BIGINT NOT NULL DEFAULT 0 COMMENT '낙관적 락 버전',
    created_at              DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '생성 일시',
    updated_at              DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '수정 일시',

    -- 제약 조건
    UNIQUE KEY uk_size_conversion_from_to_brand (from_option_dictionary_id, to_option_dictionary_id, brand_id),

    -- 인덱스
    KEY idx_size_conversion_from (from_option_dictionary_id),
    KEY idx_size_conversion_to (to_option_dictionary_id),
    KEY idx_size_conversion_category (category_type),
    KEY idx_size_conversion_brand (brand_id),
    KEY idx_size_conversion_confidence (confidence)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='사이즈 환산 테이블 (국제 사이즈 체계 간 환산 규칙)';

-- -----------------------------------------------------
-- 2. attribute_option_group_mapping 테이블 생성
-- 속성과 옵션 그룹 간 연결 (예: SIZE 속성 -> SIZE_SHOES_EU, SIZE_SHOES_US 등)
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS attribute_option_group_mapping (
    -- Primary Key
    id                      BIGINT AUTO_INCREMENT PRIMARY KEY,

    -- FK (Long FK 전략)
    attribute_id            BIGINT NOT NULL COMMENT '속성 ID (attribute 테이블의 ID)',
    option_group_id         BIGINT NOT NULL COMMENT '옵션 그룹 ID (option_group 테이블의 ID)',

    -- 매핑 정보
    is_primary              BOOLEAN NOT NULL DEFAULT FALSE COMMENT '기본 옵션 그룹 여부',
    region                  VARCHAR(10) COMMENT '지역 코드 (KR, EU, US, UK, IT, FR, JP 등)',

    -- 낙관적 락 & 감사 필드
    version                 BIGINT NOT NULL DEFAULT 0 COMMENT '낙관적 락 버전',
    created_at              DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '생성 일시',
    updated_at              DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '수정 일시',

    -- 제약 조건
    UNIQUE KEY uk_attr_option_group_mapping (attribute_id, option_group_id),

    -- 인덱스
    KEY idx_aogm_attribute (attribute_id),
    KEY idx_aogm_option_group (option_group_id),
    KEY idx_aogm_region (region),
    KEY idx_aogm_primary (attribute_id, is_primary)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='속성-옵션 그룹 매핑 테이블 (SIZE 속성과 지역별 사이즈 체계 연결)';
