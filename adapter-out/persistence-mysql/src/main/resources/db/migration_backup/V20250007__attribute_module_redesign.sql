-- =====================================================
-- V20250007: Attribute 모듈 재설계
-- =====================================================
-- 변경 내용:
--   1. attribute 테이블: 생성 (없으면) + value_type, applies_level 컬럼 추가
--   2. attribute_value 테이블: 생성 (없으면) + color_code → meta_json 변경
--   3. category_attribute_template 테이블 신규 생성
--   4. category_attribute_spec 테이블 신규 생성
-- =====================================================

-- -----------------------------------------------------
-- 1. attribute 테이블 생성 및 변경
-- -----------------------------------------------------

-- 1.0 테이블이 없으면 먼저 생성
CREATE TABLE IF NOT EXISTS attribute (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    code                VARCHAR(100) NOT NULL COMMENT '속성 고유 코드',
    name_ko             VARCHAR(255) NOT NULL COMMENT '한글 속성명',
    name_en             VARCHAR(255) COMMENT '영문 속성명',
    type                VARCHAR(50) NOT NULL COMMENT '속성 타입',
    status              VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '상태',
    version             BIGINT NOT NULL DEFAULT 0 COMMENT '낙관적 락 버전',
    created_at          DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '생성 일시',
    updated_at          DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '수정 일시',
    UNIQUE KEY uk_attribute_code (code),
    KEY idx_attribute_type (type),
    KEY idx_attribute_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='속성 마스터 테이블';

-- 1.1 value_type 컬럼 추가 (ENUM, TEXT, NUMBER, BOOLEAN, JSON)
-- 이미 존재하면 무시
SET @column_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'attribute' AND COLUMN_NAME = 'value_type');
SET @sql = IF(@column_exists = 0,
    'ALTER TABLE attribute ADD COLUMN value_type VARCHAR(20) NOT NULL DEFAULT ''ENUM'' AFTER type',
    'SELECT ''Column value_type already exists''');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 1.2 applies_level 컬럼 추가 (PRODUCT, SKU, BOTH)
SET @column_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'attribute' AND COLUMN_NAME = 'applies_level');
SET @sql = IF(@column_exists = 0,
    'ALTER TABLE attribute ADD COLUMN applies_level VARCHAR(20) NOT NULL DEFAULT ''BOTH'' AFTER value_type',
    'SELECT ''Column applies_level already exists''');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 1.3 인덱스 추가 (이미 존재하면 무시)
SET @index_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'attribute' AND INDEX_NAME = 'idx_attribute_value_type');
SET @sql = IF(@index_exists = 0,
    'CREATE INDEX idx_attribute_value_type ON attribute (value_type)',
    'SELECT ''Index idx_attribute_value_type already exists''');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @index_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'attribute' AND INDEX_NAME = 'idx_attribute_applies_level');
SET @sql = IF(@index_exists = 0,
    'CREATE INDEX idx_attribute_applies_level ON attribute (applies_level)',
    'SELECT ''Index idx_attribute_applies_level already exists''');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- -----------------------------------------------------
-- 2. attribute_value 테이블 생성 및 변경
-- -----------------------------------------------------

-- 2.0 테이블이 없으면 먼저 생성
CREATE TABLE IF NOT EXISTS attribute_value (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    attribute_id        BIGINT NOT NULL COMMENT '속성 ID',
    code                VARCHAR(100) NOT NULL COMMENT '속성값 코드',
    name_ko             VARCHAR(255) NOT NULL COMMENT '한글 속성값명',
    name_en             VARCHAR(255) COMMENT '영문 속성값명',
    sort_order          INT NOT NULL DEFAULT 0 COMMENT '정렬 순서',
    status              VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '상태',
    created_at          DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '생성 일시',
    updated_at          DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '수정 일시',
    KEY idx_attribute_value_attribute_id (attribute_id),
    KEY idx_attribute_value_code (attribute_id, code),
    KEY idx_attribute_value_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='속성값 테이블';

-- 2.1 meta_json 컬럼 추가
SET @column_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'attribute_value' AND COLUMN_NAME = 'meta_json');
SET @sql = IF(@column_exists = 0,
    'ALTER TABLE attribute_value ADD COLUMN meta_json VARCHAR(1000) AFTER sort_order',
    'SELECT ''Column meta_json already exists''');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 2.2 기존 color_code 데이터를 meta_json으로 마이그레이션 (color_code 컬럼이 존재하면)
SET @column_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'attribute_value' AND COLUMN_NAME = 'color_code');
SET @sql = IF(@column_exists > 0,
    'UPDATE attribute_value SET meta_json = CONCAT(''{"hex":"'', color_code, ''"}'') WHERE color_code IS NOT NULL AND color_code != ''''',
    'SELECT ''Column color_code does not exist, skipping migration''');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 2.3 color_code 컬럼 삭제 (존재하면)
SET @column_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'attribute_value' AND COLUMN_NAME = 'color_code');
SET @sql = IF(@column_exists > 0,
    'ALTER TABLE attribute_value DROP COLUMN color_code',
    'SELECT ''Column color_code does not exist, skipping drop''');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- -----------------------------------------------------
-- 3. category_attribute_template 테이블 생성
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS category_attribute_template (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    category_group VARCHAR(50) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    version BIGINT NOT NULL DEFAULT 0,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),

    CONSTRAINT uk_cat_attr_template_group UNIQUE (category_group)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 인덱스 추가 (이미 존재하면 무시)
SET @index_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'category_attribute_template' AND INDEX_NAME = 'idx_cat_attr_template_active');
SET @sql = IF(@index_exists = 0,
    'CREATE INDEX idx_cat_attr_template_active ON category_attribute_template (active)',
    'SELECT ''Index already exists''');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- -----------------------------------------------------
-- 4. category_attribute_spec 테이블 생성
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS category_attribute_spec (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    template_id BIGINT NOT NULL,
    attribute_id BIGINT NOT NULL,
    required BOOLEAN NOT NULL DEFAULT TRUE,
    min_selection INT,
    max_selection INT,
    validation_pattern VARCHAR(500),
    sort_order INT NOT NULL DEFAULT 0,
    default_value_ids TEXT,
    display_hint VARCHAR(500),
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),

    CONSTRAINT uk_cat_attr_spec_template_attribute UNIQUE (template_id, attribute_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 인덱스 추가 (이미 존재하면 무시)
SET @index_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'category_attribute_spec' AND INDEX_NAME = 'idx_cat_attr_spec_template');
SET @sql = IF(@index_exists = 0,
    'CREATE INDEX idx_cat_attr_spec_template ON category_attribute_spec (template_id)',
    'SELECT ''Index already exists''');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @index_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'category_attribute_spec' AND INDEX_NAME = 'idx_cat_attr_spec_attribute');
SET @sql = IF(@index_exists = 0,
    'CREATE INDEX idx_cat_attr_spec_attribute ON category_attribute_spec (attribute_id)',
    'SELECT ''Index already exists''');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @index_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'category_attribute_spec' AND INDEX_NAME = 'idx_cat_attr_spec_sort');
SET @sql = IF(@index_exists = 0,
    'CREATE INDEX idx_cat_attr_spec_sort ON category_attribute_spec (template_id, sort_order)',
    'SELECT ''Index already exists''');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
