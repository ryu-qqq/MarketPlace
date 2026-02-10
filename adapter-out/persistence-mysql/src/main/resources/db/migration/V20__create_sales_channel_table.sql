CREATE TABLE sales_channel (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    channel_name  VARCHAR(100) NOT NULL COMMENT '판매채널명',
    status        VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE' COMMENT 'ACTIVE/INACTIVE',
    created_at    DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at    DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    UNIQUE KEY uq_channel_name (channel_name),
    INDEX idx_sc_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='판매채널 마스터 테이블';

INSERT INTO sales_channel (channel_name, status) VALUES
('자사몰', 'ACTIVE'), ('네이버 스마트스토어', 'ACTIVE'), ('쿠팡', 'ACTIVE'),
('11번가', 'ACTIVE'), ('G마켓', 'ACTIVE'), ('옥션', 'ACTIVE'),
('SSG닷컴', 'ACTIVE'), ('롯데ON', 'ACTIVE'), ('인터파크', 'ACTIVE'),
('위메프', 'ACTIVE'), ('티몬', 'ACTIVE');
