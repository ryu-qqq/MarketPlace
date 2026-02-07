-- Category 테이블 생성
-- 카테고리 계층 구조를 표현하는 Self-Referencing 테이블
-- Path Enumeration 패턴 사용으로 조상 조회 성능 최적화

CREATE TABLE category (
    -- Primary Key
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,

    -- 고유 식별자
    code            VARCHAR(100) NOT NULL COMMENT '카테고리 고유 코드 (예: CAT001)',

    -- 이름 정보
    name_ko         VARCHAR(255) NOT NULL COMMENT '한글 카테고리명',
    name_en         VARCHAR(255) COMMENT '영문 카테고리명',

    -- 계층 구조 정보
    parent_id       BIGINT UNSIGNED NULL COMMENT '부모 카테고리 ID (루트는 NULL)',
    depth           TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '계층 깊이 (루트=0)',
    path            VARCHAR(1000) NOT NULL COMMENT 'Path Enumeration (예: 1/2/3)',
    sort_order      INT NOT NULL DEFAULT 0 COMMENT '정렬 순서',
    is_leaf         TINYINT(1) NOT NULL DEFAULT 1 COMMENT '리프 노드 여부',

    -- 상태 정보
    status          VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '카테고리 상태 (ACTIVE, HIDDEN, DEPRECATED)',
    is_visible      TINYINT(1) NOT NULL DEFAULT 1 COMMENT '프론트엔드 노출 여부',
    is_listable     TINYINT(1) NOT NULL DEFAULT 1 COMMENT '목록 페이지 표시 여부',

    -- 비즈니스 분류
    department      VARCHAR(50) NOT NULL DEFAULT 'FASHION' COMMENT '상품 부문 (FASHION, BEAUTY, LIVING, ETC)',
    product_group   VARCHAR(50) NOT NULL DEFAULT 'ETC' COMMENT '상품 그룹 (CLOTHING, SHOES, BAGS, ETC)',
    gender_scope    VARCHAR(20) NOT NULL DEFAULT 'NONE' COMMENT '성별 범위 (MALE, FEMALE, UNISEX, KIDS, NONE)',
    age_group       VARCHAR(20) NOT NULL DEFAULT 'NONE' COMMENT '연령대 (KIDS, TEENS, ADULT, SENIOR, NONE)',

    -- 메타 정보
    display_name    VARCHAR(255) COMMENT '표시용 이름 (프론트엔드)',
    seo_slug        VARCHAR(255) COMMENT 'SEO 친화적 URL slug',
    icon_url        VARCHAR(500) COMMENT '카테고리 아이콘 URL',

    -- 낙관적 락 & 감사 필드
    version         BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '낙관적 락 버전',
    created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 일시',
    updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 일시',

    -- 제약 조건
    UNIQUE KEY uk_category_code (code),

    -- 인덱스: 계층 조회 성능 최적화
    KEY idx_category_parent (parent_id),
    KEY idx_category_parent_sort (parent_id, sort_order),
    KEY idx_category_status (status, is_visible),
    KEY idx_category_business (department, product_group, gender_scope),
    KEY idx_category_path (path(255)),
    KEY idx_category_updated (updated_at),

    -- 외래 키: 부모 카테고리 참조
    CONSTRAINT fk_category_parent FOREIGN KEY (parent_id) REFERENCES category(id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='카테고리 계층 구조 테이블 (Path Enumeration 패턴)';

-- 초기 데이터: 루트 카테고리 (예시)
-- INSERT INTO category (code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, department, product_group)
-- VALUES ('ROOT', '전체 카테고리', 'All Categories', NULL, 0, '1', 0, 0, 'ACTIVE', 'FASHION', 'ETC');
