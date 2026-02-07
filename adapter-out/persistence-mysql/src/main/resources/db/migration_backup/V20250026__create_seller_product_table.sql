-- SellerProduct 모듈 테이블 생성
-- 외부 상품 데이터 Import 및 매칭 관리

-- seller_product 테이블
CREATE TABLE seller_product (
    id                      BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    seller_id               BIGINT UNSIGNED NOT NULL COMMENT '셀러 ID',

    -- SourceInfo (외부 상품 출처 정보)
    sales_channel_id        BIGINT UNSIGNED NOT NULL COMMENT '판매 채널 ID',
    external_product_id     VARCHAR(255) NOT NULL COMMENT '외부 상품 ID (판매 채널 내 고유 식별자)',
    imported_at             TIMESTAMP NOT NULL COMMENT '최초 임포트 시각',
    last_synced_at          TIMESTAMP NULL COMMENT '마지막 동기화 시각',

    -- 외부 상품 코드
    external_code           VARCHAR(255) NOT NULL COMMENT '외부 상품 코드 (셀러 상품 코드)',

    -- 원본 데이터 (JSON)
    raw_payload_json        MEDIUMTEXT NOT NULL COMMENT '외부 시스템 원본 JSON 데이터',

    -- 정규화된 상품 데이터 (JSON)
    normalized_data_json    MEDIUMTEXT NULL COMMENT '정규화된 상품 데이터 (brandId, categoryId, price 등)',

    -- 매칭 정보
    matching_status         VARCHAR(30) NOT NULL DEFAULT 'UNMATCHED' COMMENT '매칭 상태 (UNMATCHED, PENDING_REVIEW, MATCHED, NEW_CREATED, REJECTED)',
    matched_product_id      BIGINT UNSIGNED NULL COMMENT '매칭된 표준 상품 ID',
    confidence              DOUBLE NULL COMMENT '매칭 신뢰도 (0.0 ~ 1.0)',

    -- 버전 관리
    version                 BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '낙관적 락 버전',

    -- 감사 필드
    created_at              DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at              DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    -- 인덱스
    CONSTRAINT uk_seller_product_external UNIQUE (seller_id, external_code),
    INDEX idx_seller_product_seller (seller_id),
    INDEX idx_seller_product_channel (sales_channel_id),
    INDEX idx_seller_product_status (matching_status),
    INDEX idx_seller_product_matched (matched_product_id),
    INDEX idx_seller_product_imported (imported_at),
    INDEX idx_seller_product_updated (updated_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='외부 상품 (셀러 원본 상품 데이터 저장)';

-- seller_product_change_log 테이블 (변경 이력 추적)
CREATE TABLE seller_product_change_log (
    id                      BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    seller_product_id       BIGINT UNSIGNED NOT NULL COMMENT '외부 상품 ID',
    change_type             VARCHAR(50) NOT NULL COMMENT '변경 유형 (CREATED, UPDATED, STATUS_CHANGED, MATCHED)',
    field_name              VARCHAR(100) NULL COMMENT '변경된 필드명',
    previous_value          TEXT NULL COMMENT '이전 값',
    new_value               TEXT NULL COMMENT '새 값',
    changed_by              VARCHAR(100) NOT NULL DEFAULT 'SYSTEM' COMMENT '변경 주체',
    changed_at              DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '변경 시각',

    INDEX idx_change_log_product (seller_product_id),
    INDEX idx_change_log_type (change_type),
    INDEX idx_change_log_changed_at (changed_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='외부 상품 변경 이력';
