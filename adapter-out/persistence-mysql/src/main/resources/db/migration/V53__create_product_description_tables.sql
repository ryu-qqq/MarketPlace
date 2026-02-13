-- ============================================
-- 상품 상세설명 테이블
-- product_group_descriptions, description_images
-- ============================================

-- ========================================
-- product_group_descriptions 테이블
-- ========================================

CREATE TABLE IF NOT EXISTS product_group_descriptions (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'PK',
    product_group_id BIGINT NOT NULL COMMENT '상품 그룹 ID',
    content TEXT NULL COMMENT '상세설명 HTML 콘텐츠',
    cdn_path VARCHAR(500) NULL COMMENT 'CDN 경로',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    PRIMARY KEY (id),
    INDEX idx_product_group_descriptions_product_group_id (product_group_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='상품 그룹 상세설명';

-- ========================================
-- description_images 테이블
-- ========================================

CREATE TABLE IF NOT EXISTS description_images (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'PK',
    product_group_description_id BIGINT NOT NULL COMMENT '상품 그룹 상세설명 ID',
    origin_url VARCHAR(500) NOT NULL COMMENT '원본 이미지 URL',
    uploaded_url VARCHAR(500) NULL COMMENT '업로드된 이미지 URL',
    sort_order INT NOT NULL COMMENT '정렬 순서',
    PRIMARY KEY (id),
    INDEX idx_description_images_description_id (product_group_description_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='상세설명 이미지';
