-- ================================================
-- V20250013: SalesChannelOptionMapping 테이블 생성
-- 판매채널별 옵션 매핑 테이블
-- ================================================

CREATE TABLE IF NOT EXISTS sales_channel_option_mapping (
    -- Primary Key
    id                      BIGINT AUTO_INCREMENT PRIMARY KEY,

    -- FK (Long FK 전략)
    sales_channel_id        BIGINT NOT NULL COMMENT '판매채널 ID',
    option_group_id         BIGINT NOT NULL COMMENT '옵션 그룹 ID',
    option_dictionary_id    BIGINT NOT NULL COMMENT '옵션 사전 ID',

    -- 채널별 옵션 정보
    channel_option_code     VARCHAR(100) NOT NULL COMMENT '채널 옵션 코드',
    channel_option_value    VARCHAR(500) NOT NULL COMMENT '채널 옵션 값',
    is_default              BOOLEAN NOT NULL DEFAULT FALSE COMMENT '기본 옵션 여부',

    -- 낙관적 락 & 감사 필드
    version                 BIGINT NOT NULL DEFAULT 0 COMMENT '낙관적 락 버전',
    created_at              DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '생성 일시',
    updated_at              DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '수정 일시',

    -- 제약 조건
    UNIQUE KEY uk_sales_channel_option_mapping (sales_channel_id, option_group_id, option_dictionary_id),

    -- 인덱스
    KEY idx_scm_sales_channel (sales_channel_id),
    KEY idx_scm_option_group (option_group_id),
    KEY idx_scm_option_dictionary (option_dictionary_id),
    KEY idx_scm_channel_code (sales_channel_id, channel_option_code),
    KEY idx_scm_is_default (sales_channel_id, is_default)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='판매채널별 옵션 매핑 테이블';
