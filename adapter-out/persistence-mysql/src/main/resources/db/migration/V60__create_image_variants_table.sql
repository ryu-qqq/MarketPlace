CREATE TABLE IF NOT EXISTS image_variants (
    id BIGINT NOT NULL AUTO_INCREMENT,
    source_image_id BIGINT NOT NULL COMMENT '원본 이미지 ID',
    source_type VARCHAR(30) NOT NULL COMMENT 'PRODUCT_GROUP_IMAGE, DESCRIPTION_IMAGE',
    variant_type VARCHAR(30) NOT NULL COMMENT 'SMALL_WEBP, MEDIUM_WEBP, LARGE_WEBP, ORIGINAL_WEBP',
    result_asset_id VARCHAR(100) NOT NULL COMMENT 'FileFlow 변환 에셋 ID',
    variant_url VARCHAR(500) NOT NULL COMMENT '변환된 이미지 CDN URL',
    width INT NULL,
    height INT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_iv_source_variant (source_image_id, source_type, variant_type),
    INDEX idx_iv_source (source_type, source_image_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
