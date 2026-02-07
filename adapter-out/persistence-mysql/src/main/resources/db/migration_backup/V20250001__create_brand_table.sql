-- Brand 모듈 테이블 생성
-- 브랜드 표준화(Canonical Brand) 및 별칭(Alias) 관리

-- brand 테이블
CREATE TABLE brand (
    id                  BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    code                VARCHAR(100) NOT NULL,
    canonical_name      VARCHAR(255) NOT NULL,
    name_ko             VARCHAR(255),
    name_en             VARCHAR(255),
    short_name          VARCHAR(100),
    country             VARCHAR(10),
    department          VARCHAR(50) NOT NULL DEFAULT 'FASHION',
    is_luxury           TINYINT(1) NOT NULL DEFAULT 0,
    status              VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    official_website    VARCHAR(500),
    logo_url            VARCHAR(500),
    description         TEXT,
    data_quality_level  VARCHAR(50) NOT NULL DEFAULT 'UNKNOWN',
    data_quality_score  INT NOT NULL DEFAULT 0,
    version             BIGINT UNSIGNED NOT NULL DEFAULT 0,
    created_at          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT uk_brand_code UNIQUE (code),
    CONSTRAINT uk_brand_canonical_name UNIQUE (canonical_name),
    INDEX idx_brand_status (status),
    INDEX idx_brand_department (department),
    INDEX idx_brand_country (country),
    INDEX idx_brand_luxury (is_luxury),
    INDEX idx_brand_updated (updated_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='표준 브랜드 마스터 테이블';

-- brand_alias 테이블
CREATE TABLE brand_alias (
    id                  BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    brand_id            BIGINT UNSIGNED NOT NULL,
    alias_name          VARCHAR(255) NOT NULL,
    normalized_alias    VARCHAR(255) NOT NULL,
    source_type         VARCHAR(50) NOT NULL DEFAULT 'MANUAL',
    seller_id           BIGINT UNSIGNED NOT NULL DEFAULT 0,
    mall_code           VARCHAR(50) NOT NULL DEFAULT 'GLOBAL',
    confidence          DECIMAL(5,4) NOT NULL DEFAULT 1.0000,
    status              VARCHAR(30) NOT NULL DEFAULT 'CONFIRMED',
    created_at          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_brand_alias_brand FOREIGN KEY (brand_id) REFERENCES brand(id) ON DELETE CASCADE,
    CONSTRAINT uk_brand_alias_scope UNIQUE (brand_id, normalized_alias, mall_code, seller_id),
    INDEX idx_brand_alias_normalized (normalized_alias),
    INDEX idx_brand_alias_brand (brand_id),
    INDEX idx_brand_alias_status (status),
    INDEX idx_brand_alias_source (source_type, seller_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='브랜드 별칭 매핑 테이블';
