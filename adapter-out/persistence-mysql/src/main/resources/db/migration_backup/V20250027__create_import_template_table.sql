-- ImportTemplate 모듈 테이블 생성
-- JSON 기반 외부 상품 데이터 파싱 템플릿 관리

-- import_template 테이블
CREATE TABLE import_template (
    id                          BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    code                        VARCHAR(100) NOT NULL COMMENT '템플릿 코드 (고유 식별자)',
    name                        VARCHAR(200) NOT NULL COMMENT '템플릿 이름',
    template_type               VARCHAR(30) NOT NULL COMMENT '템플릿 유형 (PLATFORM_DEFAULT, SELLER_CUSTOM)',
    seller_id                   BIGINT UNSIGNED NULL COMMENT '셀러 ID (SELLER_CUSTOM인 경우 필수)',
    status                      VARCHAR(30) NOT NULL DEFAULT 'DRAFT' COMMENT '템플릿 상태 (DRAFT, ACTIVE, DEPRECATED)',

    -- 버전 정보 (Semantic Versioning)
    version_major               INT NOT NULL DEFAULT 1 COMMENT '메이저 버전 (하위 호환 불가 변경)',
    version_minor               INT NOT NULL DEFAULT 0 COMMENT '마이너 버전 (선택 필드 추가/변경)',
    version_patch               INT NOT NULL DEFAULT 0 COMMENT '패치 버전 (버그 수정)',

    -- 필드 매핑 (JSON)
    field_mappings_json         MEDIUMTEXT NOT NULL COMMENT '필드 매핑 정보 (JSONPath 기반)',
    custom_field_mappings_json  MEDIUMTEXT NULL COMMENT '커스텀 필드 매핑 정보 (SELLER_CUSTOM 전용)',

    -- 설명
    description                 VARCHAR(1000) NULL COMMENT '템플릿 설명',

    -- 버전 관리
    entity_version              BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '낙관적 락 버전',

    -- 감사 필드
    created_at                  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    -- 인덱스
    CONSTRAINT uk_import_template_code UNIQUE (code),
    INDEX idx_import_template_type (template_type),
    INDEX idx_import_template_seller (seller_id),
    INDEX idx_import_template_status (status),
    INDEX idx_import_template_updated (updated_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='외부 상품 임포트 템플릿';

-- 플랫폼 기본 템플릿 초기 데이터
INSERT INTO import_template (
    code, name, template_type, seller_id, status,
    version_major, version_minor, version_patch,
    field_mappings_json, custom_field_mappings_json, description
) VALUES (
    'PLATFORM_STANDARD_V1',
    '플랫폼 표준 템플릿 V1',
    'PLATFORM_DEFAULT',
    NULL,
    'ACTIVE',
    1, 0, 0,
    '[{"targetField":"productName","sourcePath":"$.name","dataType":"STRING","defaultValue":null,"required":true},{"targetField":"brandName","sourcePath":"$.brand","dataType":"STRING","defaultValue":null,"required":true},{"targetField":"categoryName","sourcePath":"$.category","dataType":"STRING","defaultValue":null,"required":true},{"targetField":"basePrice","sourcePath":"$.price.base","dataType":"DECIMAL","defaultValue":null,"required":true},{"targetField":"salePrice","sourcePath":"$.price.sale","dataType":"DECIMAL","defaultValue":null,"required":false},{"targetField":"description","sourcePath":"$.description","dataType":"STRING","defaultValue":null,"required":false},{"targetField":"images","sourcePath":"$.images[*]","dataType":"ARRAY","defaultValue":null,"required":false}]',
    '[]',
    '플랫폼에서 제공하는 기본 외부 상품 임포트 템플릿입니다.'
);
