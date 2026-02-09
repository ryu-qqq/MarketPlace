CREATE TABLE shop (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    shop_name   VARCHAR(100) NOT NULL COMMENT '외부몰명',
    account_id  VARCHAR(100) NOT NULL COMMENT '계정 ID',
    status      VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE' COMMENT 'ACTIVE/INACTIVE',
    created_at  DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at  DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    deleted_at  DATETIME(6)  NULL,

    INDEX idx_shop_status (status),
    INDEX idx_shop_name (shop_name),
    INDEX idx_shop_updated (updated_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='외부몰 마스터 테이블';
