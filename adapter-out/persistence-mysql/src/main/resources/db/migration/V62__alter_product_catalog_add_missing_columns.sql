-- ============================================
-- 상품 카탈로그 테이블 누락 컬럼 추가
-- description_images: deleted, deleted_at (soft delete)
-- product_group_descriptions: publish_status
-- product_group_images: deleted, deleted_at (soft delete)
-- ============================================

-- ========================================
-- description_images: soft delete 컬럼 추가
-- ========================================
ALTER TABLE description_images
    ADD COLUMN deleted BOOLEAN NOT NULL DEFAULT FALSE COMMENT '삭제 여부' AFTER sort_order,
    ADD COLUMN deleted_at TIMESTAMP NULL COMMENT '삭제일시' AFTER deleted;

-- ========================================
-- product_group_descriptions: publish_status 컬럼 추가
-- ========================================
ALTER TABLE product_group_descriptions
    ADD COLUMN publish_status VARCHAR(30) NOT NULL DEFAULT 'DRAFT' COMMENT '발행 상태' AFTER cdn_path;

-- ========================================
-- product_group_images: soft delete 컬럼 추가
-- ========================================
ALTER TABLE product_group_images
    ADD COLUMN deleted BOOLEAN NOT NULL DEFAULT FALSE COMMENT '삭제 여부' AFTER sort_order,
    ADD COLUMN deleted_at TIMESTAMP NULL COMMENT '삭제일시' AFTER deleted;
