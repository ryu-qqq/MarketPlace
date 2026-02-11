-- ============================================
-- 캐노니컬 옵션 그룹 / 옵션 값 테이블
-- ============================================
-- 시스템 관리 read-only 마스터 데이터
-- 셀러 자유입력 옵션 → 캐노니컬 표준 → 채널별 매핑
-- ============================================

-- ============================================
-- 1. canonical_option_group (옵션 그룹 마스터)
-- ============================================
CREATE TABLE canonical_option_group (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    code        VARCHAR(50)  NOT NULL,
    name_ko     VARCHAR(100) NOT NULL,
    name_en     VARCHAR(100),
    active      BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_canonical_option_group_code (code)
);

-- ============================================
-- 2. canonical_option_value (옵션 값 마스터)
-- ============================================
CREATE TABLE canonical_option_value (
    id                          BIGINT PRIMARY KEY AUTO_INCREMENT,
    canonical_option_group_id   BIGINT       NOT NULL,
    code                        VARCHAR(50)  NOT NULL,
    name_ko                     VARCHAR(100) NOT NULL,
    name_en                     VARCHAR(100),
    sort_order                  INT          NOT NULL DEFAULT 0,
    created_at                  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_canonical_value_group FOREIGN KEY (canonical_option_group_id) REFERENCES canonical_option_group(id),
    UNIQUE KEY uk_canonical_value_group_code (canonical_option_group_id, code)
);
