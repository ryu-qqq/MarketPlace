-- ============================================
-- 셀러 판매채널 연동 테이블
-- 셀러별 외부 판매채널 연동 정보 관리
-- ============================================

CREATE TABLE IF NOT EXISTS seller_sales_channels (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'PK',
    seller_id BIGINT NOT NULL COMMENT '셀러 ID',
    sales_channel_id BIGINT NOT NULL COMMENT '판매채널 ID',
    channel_code VARCHAR(50) NOT NULL COMMENT '채널 코드 (NAVER_COMMERCE, SETOF, BUYMA, LFMALL)',
    connection_status VARCHAR(20) NOT NULL COMMENT '연동 상태 (CONNECTED, DISCONNECTED, SUSPENDED)',
    api_key VARCHAR(500) NULL COMMENT 'API Key',
    api_secret VARCHAR(500) NULL COMMENT 'API Secret',
    access_token VARCHAR(1000) NULL COMMENT 'Access Token',
    vendor_id VARCHAR(100) NULL COMMENT '외부 벤더 ID',
    display_name VARCHAR(200) NULL COMMENT '표시명',
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '생성일시',
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '수정일시',
    PRIMARY KEY (id),
    UNIQUE KEY uk_seller_sales_channels_seller_channel (seller_id, sales_channel_id),
    INDEX idx_seller_sales_channels_seller_id (seller_id),
    INDEX idx_seller_sales_channels_connection_status (connection_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='셀러 판매채널 연동';
