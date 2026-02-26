-- 고시정보 (Notice) 관련 테이블 생성
-- 상품 등록 시 법정 고시정보 카테고리 및 필드 관리
-- 기준: 전자상거래 등에서의 상품 등의 정보제공에 관한 고시 (공정거래위원회 고시 제2023-18호)

-- ============================================================
-- notice_category 테이블
-- 고시정보 카테고리 (의류, 가방, 신발, 화장품 등)
-- CategoryGroup enum과 1:1 매핑
-- ============================================================

CREATE TABLE notice_category (
    id                      BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    code                    VARCHAR(50) NOT NULL COMMENT '고시정보 카테고리 코드 (CategoryGroup enum 값과 동일)',
    name_ko                 VARCHAR(100) NOT NULL COMMENT '한국어 이름',
    name_en                 VARCHAR(100) COMMENT '영어 이름',
    target_category_group   VARCHAR(50) NOT NULL COMMENT '적용 대상 카테고리 그룹 (CLOTHING, SHOES, COSMETICS 등)',
    active                  TINYINT(1) NOT NULL DEFAULT 1 COMMENT '활성 상태',
    created_at              DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at              DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT uk_notice_category_code UNIQUE (code),
    CONSTRAINT uk_notice_category_group UNIQUE (target_category_group),
    INDEX idx_notice_category_active (active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='고시정보 카테고리 테이블';


-- ============================================================
-- notice_field 테이블
-- 고시정보 필드 (제조국, 제조자, 소재 등)
-- ============================================================

CREATE TABLE notice_field (
    id                      BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    notice_category_id      BIGINT UNSIGNED NOT NULL COMMENT '고시정보 카테고리 ID',
    field_code              VARCHAR(50) NOT NULL COMMENT '필드 코드',
    field_name              VARCHAR(100) NOT NULL COMMENT '필드 이름 (표시명)',
    required                TINYINT(1) NOT NULL DEFAULT 0 COMMENT '필수 여부',
    sort_order              INT NOT NULL DEFAULT 0 COMMENT '정렬 순서',
    created_at              DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at              DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT uk_notice_field_category_code UNIQUE (notice_category_id, field_code),
    INDEX idx_notice_field_category (notice_category_id),
    INDEX idx_notice_field_sort (notice_category_id, sort_order),
    CONSTRAINT fk_notice_field_category FOREIGN KEY (notice_category_id) REFERENCES notice_category (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='고시정보 필드 테이블';
