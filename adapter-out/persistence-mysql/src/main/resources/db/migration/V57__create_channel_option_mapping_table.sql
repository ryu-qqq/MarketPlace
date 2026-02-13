-- ============================================
-- 판매 채널 옵션 매핑 테이블
-- channel_option_mapping
-- ============================================

CREATE TABLE IF NOT EXISTS channel_option_mapping (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'PK',
    sales_channel_id BIGINT NOT NULL COMMENT '판매 채널 ID',
    canonical_option_value_id BIGINT NOT NULL COMMENT '표준 옵션값 ID',
    external_option_code VARCHAR(100) NOT NULL COMMENT '외부 옵션 코드',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    PRIMARY KEY (id),
    INDEX idx_channel_option_mapping_channel_id (sales_channel_id),
    INDEX idx_channel_option_mapping_canonical_id (canonical_option_value_id),
    UNIQUE KEY uk_channel_option_mapping (sales_channel_id, canonical_option_value_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='판매 채널 옵션 매핑';
