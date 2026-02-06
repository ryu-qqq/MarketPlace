-- ================================================
-- V20250012: Option 관련 테이블 생성
-- option_group, option_dictionary, option_alias 테이블
-- ================================================

-- -----------------------------------------------------
-- 1. option_group 테이블 생성
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS option_group (
    -- Primary Key
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,

    -- 고유 식별자
    code                VARCHAR(50) NOT NULL COMMENT '옵션 그룹 코드 (예: COLOR, SIZE)',

    -- 이름 정보
    name_ko             VARCHAR(100) NOT NULL COMMENT '한글 옵션 그룹명',
    name_en             VARCHAR(100) COMMENT '영문 옵션 그룹명',

    -- 타입 및 상태 정보
    type                VARCHAR(30) NOT NULL COMMENT '옵션 그룹 타입 (BASIC, COMBINED)',
    status              VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '상태 (ACTIVE, INACTIVE)',

    -- 추가 정보
    description         VARCHAR(500) COMMENT '옵션 그룹 설명',
    sort_order          INT NOT NULL DEFAULT 0 COMMENT '정렬 순서',

    -- 낙관적 락 & 감사 필드
    version             BIGINT NOT NULL DEFAULT 0 COMMENT '낙관적 락 버전',
    created_at          DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '생성 일시',
    updated_at          DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '수정 일시',

    -- 제약 조건
    UNIQUE KEY uk_option_group_code (code),

    -- 인덱스
    KEY idx_option_group_type (type),
    KEY idx_option_group_status (status),
    KEY idx_option_group_sort (sort_order)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='옵션 그룹 테이블';

-- -----------------------------------------------------
-- 2. option_dictionary 테이블 생성
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS option_dictionary (
    -- Primary Key
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,

    -- FK (Long FK 전략)
    option_group_id     BIGINT NOT NULL COMMENT '옵션 그룹 ID',

    -- 옵션 값 정보
    canonical_value     VARCHAR(100) NOT NULL COMMENT '정규화된 옵션 값 (예: RED, XL)',
    display_name_ko     VARCHAR(100) NOT NULL COMMENT '한글 표시명',
    display_name_en     VARCHAR(100) COMMENT '영문 표시명',

    -- 상태 및 정렬
    status              VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '상태 (ACTIVE, INACTIVE)',
    sort_order          INT NOT NULL DEFAULT 0 COMMENT '정렬 순서',

    -- 낙관적 락 & 감사 필드
    version             BIGINT NOT NULL DEFAULT 0 COMMENT '낙관적 락 버전',
    created_at          DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '생성 일시',
    updated_at          DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '수정 일시',

    -- 제약 조건
    UNIQUE KEY uk_option_dictionary_group_value (option_group_id, canonical_value),

    -- 인덱스
    KEY idx_option_dictionary_group_id (option_group_id),
    KEY idx_option_dictionary_status (status),
    KEY idx_option_dictionary_sort (option_group_id, sort_order)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='옵션 사전 테이블 (정규화된 옵션 값)';

-- -----------------------------------------------------
-- 3. option_alias 테이블 생성
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS option_alias (
    -- Primary Key
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,

    -- FK (Long FK 전략)
    option_dictionary_id BIGINT NOT NULL COMMENT '옵션 사전 ID',

    -- 별칭 정보
    alias_value         VARCHAR(100) NOT NULL COMMENT '별칭 값 (예: 레드, 빨강, R)',
    source_type         VARCHAR(30) NOT NULL COMMENT '별칭 소스 타입 (SYSTEM, SALES_CHANNEL, USER)',
    sales_channel_id    BIGINT COMMENT '판매채널 ID (채널별 별칭인 경우)',

    -- 낙관적 락 & 감사 필드
    version             BIGINT NOT NULL DEFAULT 0 COMMENT '낙관적 락 버전',
    created_at          DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '생성 일시',
    updated_at          DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '수정 일시',

    -- 제약 조건 (동일 사전 내 같은 별칭 중복 방지)
    UNIQUE KEY uk_option_alias_dict_alias (option_dictionary_id, alias_value),

    -- 인덱스
    KEY idx_option_alias_dictionary_id (option_dictionary_id),
    KEY idx_option_alias_source_type (source_type),
    KEY idx_option_alias_sales_channel (sales_channel_id),
    KEY idx_option_alias_value (alias_value)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='옵션 별칭 테이블 (다양한 표현을 정규화된 값으로 매핑)';
