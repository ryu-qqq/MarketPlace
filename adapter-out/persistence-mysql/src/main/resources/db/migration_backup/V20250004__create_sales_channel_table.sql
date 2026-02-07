-- SalesChannel 모듈 테이블 생성
-- 판매 채널 마스터 데이터 관리

-- sales_channel 테이블
CREATE TABLE sales_channel (
    id                  BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    code                VARCHAR(50) NOT NULL COMMENT '채널 코드 (COUPANG, NAVER 등)',
    name                VARCHAR(100) NOT NULL COMMENT '채널명',
    type                VARCHAR(20) NOT NULL DEFAULT 'EXTERNAL' COMMENT '채널 유형 (INTERNAL/EXTERNAL)',
    description         VARCHAR(500) COMMENT '채널 설명',
    status              VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '상태 (ACTIVE/INACTIVE/MAINTENANCE)',
    version             BIGINT UNSIGNED NOT NULL DEFAULT 0,
    created_at          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT uk_sales_channel_code UNIQUE (code),
    INDEX idx_sales_channel_status (status),
    INDEX idx_sales_channel_type (type),
    INDEX idx_sales_channel_updated (updated_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='판매 채널 마스터 테이블';
