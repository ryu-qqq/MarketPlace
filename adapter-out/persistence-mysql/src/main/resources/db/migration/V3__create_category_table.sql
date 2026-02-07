-- ============================================
-- 카테고리 테이블 (Path Enumeration 패턴)
-- ============================================

CREATE TABLE IF NOT EXISTS category (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'PK',
    code VARCHAR(100) NOT NULL COMMENT '카테고리 고유 코드 (예: CAT001)',
    name_ko VARCHAR(255) NOT NULL COMMENT '한글 카테고리명',
    name_en VARCHAR(255) NULL COMMENT '영문 카테고리명',
    parent_id BIGINT UNSIGNED NULL COMMENT '부모 카테고리 ID (루트는 NULL)',
    depth TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '계층 깊이 (루트=0)',
    path VARCHAR(1000) NOT NULL COMMENT 'Path Enumeration (예: 1/2/3)',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '정렬 순서',
    leaf TINYINT(1) NOT NULL DEFAULT 0 COMMENT '리프 노드 여부',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '카테고리 상태 (ACTIVE, HIDDEN, DEPRECATED)',
    department VARCHAR(30) NOT NULL COMMENT '부서/부문 (MEN, WOMEN, UNISEX 등)',
    category_group VARCHAR(50) NOT NULL DEFAULT 'ETC' COMMENT '카테고리 그룹 (고시정보 연결용)',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    deleted_at TIMESTAMP NULL DEFAULT NULL COMMENT '삭제일시 (soft delete)',
    PRIMARY KEY (id),
    UNIQUE KEY uk_category_code (code),
    KEY idx_category_parent (parent_id),
    KEY idx_category_parent_sort (parent_id, sort_order),
    KEY idx_category_status (status),
    KEY idx_category_business (department, category_group),
    KEY idx_category_path (path(255)),
    KEY idx_category_updated (updated_at),
    KEY idx_category_group (category_group),
    KEY idx_category_deleted (deleted_at),
    CONSTRAINT fk_category_parent FOREIGN KEY (parent_id) REFERENCES category (id) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='카테고리 계층 구조 테이블 (Path Enumeration 패턴)';
