CREATE TABLE sales_channel_category (
    id                      BIGINT AUTO_INCREMENT PRIMARY KEY,
    sales_channel_id        BIGINT       NOT NULL COMMENT '판매채널 ID',
    external_category_code  VARCHAR(200) NOT NULL COMMENT '외부 카테고리 코드',
    external_category_name  VARCHAR(500) NOT NULL COMMENT '외부 카테고리명',
    parent_id               BIGINT       NULL     COMMENT '부모 카테고리 ID (self-ref)',
    depth                   INT          NOT NULL DEFAULT 0,
    path                    VARCHAR(1000) NOT NULL,
    sort_order              INT          NOT NULL DEFAULT 0,
    leaf                    TINYINT(1)   NOT NULL DEFAULT 0,
    status                  VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    created_at              DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at              DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    UNIQUE KEY uq_scc_sc_code (sales_channel_id, external_category_code),
    INDEX idx_scc_sc_parent (sales_channel_id, parent_id),
    INDEX idx_scc_sc_depth (sales_channel_id, depth),
    INDEX idx_scc_path (path(255))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='외부 채널 카테고리 매핑 테이블';
