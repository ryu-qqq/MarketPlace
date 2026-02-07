-- =============================================================================
-- V20250013 - Product Change Log Table
-- 상품 변경 이력 테이블
-- =============================================================================

CREATE TABLE IF NOT EXISTS product_change_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '변경 이력 ID',
    product_id BIGINT NOT NULL COMMENT '상품 ID',
    change_type VARCHAR(30) NOT NULL COMMENT '변경 유형 (CREATE, UPDATE_INFO, UPDATE_PRICE, STATUS_CHANGE, SKU_ADD, SKU_REMOVE, SKU_UPDATE, IMAGE_ADD, IMAGE_REMOVE, NOTICE_UPDATE, CATEGORY_CHANGE, BRAND_CHANGE, DELETE)',
    changed_fields_json TEXT COMMENT '변경된 필드 목록 (JSON)',
    reason VARCHAR(500) COMMENT '변경 사유',
    changed_by_user_id BIGINT NOT NULL COMMENT '변경자 ID',
    changed_by_actor_type VARCHAR(20) NOT NULL COMMENT '변경자 유형 (SYSTEM, ADMIN, SELLER, USER)',
    changed_at TIMESTAMP(6) NOT NULL COMMENT '변경 시각',
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '생성일시',
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '수정일시',

    INDEX idx_product_change_log_product_id (product_id),
    INDEX idx_product_change_log_product_id_changed_at (product_id, changed_at DESC),
    INDEX idx_product_change_log_change_type (product_id, change_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='상품 변경 이력';
