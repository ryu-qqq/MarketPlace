-- ============================================
-- 레거시 상세설명 테이블 구조 개선
-- product_group_detail_description 컬럼 추가
-- legacy_description_images 테이블 생성
-- ============================================

-- 1. 기존 테이블에 content, cdn_path, publish_status 컬럼 추가
ALTER TABLE product_group_detail_description
    ADD COLUMN content MEDIUMTEXT NULL COMMENT '상세설명 HTML 콘텐츠',
    ADD COLUMN cdn_path VARCHAR(500) NULL COMMENT 'CDN 경로',
    ADD COLUMN publish_status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '퍼블리시 상태';

-- 2. 기존 데이터 마이그레이션: IMAGE_URL → content
UPDATE product_group_detail_description
SET content = IMAGE_URL
WHERE content IS NULL AND IMAGE_URL IS NOT NULL;

-- 3. 상세설명 이미지 테이블 생성
CREATE TABLE IF NOT EXISTS legacy_description_images (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'PK',
    product_group_id BIGINT NOT NULL COMMENT '상품그룹 ID',
    origin_url VARCHAR(500) NOT NULL COMMENT '원본 이미지 URL',
    uploaded_url VARCHAR(500) NULL COMMENT '업로드된 이미지 URL',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '정렬 순서',
    deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '삭제 여부',
    deleted_at TIMESTAMP NULL COMMENT '삭제 시각',
    PRIMARY KEY (id),
    INDEX idx_legacy_desc_images_pg_id (product_group_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='레거시 상세설명 이미지';
