CREATE TABLE IF NOT EXISTS claim_histories (
    id              VARCHAR(36)     NOT NULL COMMENT '이력 ID (UUIDv7)',
    claim_type      VARCHAR(20)     NOT NULL COMMENT 'CANCEL, REFUND, EXCHANGE',
    claim_id        VARCHAR(36)     NOT NULL COMMENT '클레임 ID (FK 없음, 유연성)',
    history_type    VARCHAR(20)     NOT NULL COMMENT 'STATUS_CHANGE, MANUAL',
    title           VARCHAR(100)    NOT NULL COMMENT '제목',
    message         VARCHAR(1000)   NOT NULL COMMENT '메시지',
    actor_type      VARCHAR(20)     NOT NULL COMMENT 'CUSTOMER, SELLER, ADMIN, SYSTEM',
    actor_id        VARCHAR(100)    NOT NULL COMMENT '액터 ID',
    actor_name      VARCHAR(100)    NOT NULL COMMENT '액터 이름',
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_claim_histories_claim (claim_type, claim_id),
    KEY idx_claim_histories_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='클레임 상태 변경/메모 이력';
