CREATE TABLE category_preset (
    id                          BIGINT AUTO_INCREMENT PRIMARY KEY,
    shop_id                     BIGINT       NOT NULL COMMENT 'Shop FK',
    sales_channel_category_id   BIGINT       NOT NULL COMMENT 'SalesChannelCategory FK',
    preset_name                 VARCHAR(200) NOT NULL COMMENT '프리셋 이름',
    status                      VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    created_at                  DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at                  DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    INDEX idx_cp_shop (shop_id),
    INDEX idx_cp_scc (sales_channel_category_id),
    INDEX idx_cp_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='카테고리 프리셋 테이블';
