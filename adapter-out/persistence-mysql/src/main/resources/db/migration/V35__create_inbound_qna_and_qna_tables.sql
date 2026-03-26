-- InboundQna: 외부 판매채널에서 수신한 QnA 원본
CREATE TABLE inbound_qnas (
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    sales_channel_id   BIGINT       NOT NULL,
    external_qna_id    VARCHAR(100) NOT NULL,
    qna_type           VARCHAR(30)  NOT NULL,
    question_content   TEXT         NOT NULL,
    question_author    VARCHAR(100) NOT NULL,
    raw_payload        TEXT,
    status             VARCHAR(30)  NOT NULL DEFAULT 'RECEIVED',
    internal_qna_id    BIGINT,
    failure_reason     VARCHAR(500),
    created_at         TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at         TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    INDEX idx_inbound_qnas_status (status),
    INDEX idx_inbound_qnas_sales_channel_external (sales_channel_id, external_qna_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Qna: 내부 QnA 도메인
CREATE TABLE qnas (
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    seller_id          BIGINT       NOT NULL,
    product_group_id   BIGINT       NOT NULL,
    qna_type           VARCHAR(30)  NOT NULL,
    sales_channel_id   BIGINT       NOT NULL,
    external_qna_id    VARCHAR(100) NOT NULL,
    question_content   TEXT         NOT NULL,
    question_author    VARCHAR(100) NOT NULL,
    status             VARCHAR(30)  NOT NULL DEFAULT 'PENDING',
    created_at         TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at         TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    INDEX idx_qnas_seller_status (seller_id, status),
    INDEX idx_qnas_product_group (product_group_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- QnaReply: QnA 답변/대댓글
CREATE TABLE qna_replies (
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    qna_id             BIGINT       NOT NULL,
    parent_reply_id    BIGINT,
    content            TEXT         NOT NULL,
    author_name        VARCHAR(100) NOT NULL,
    reply_type         VARCHAR(30)  NOT NULL,
    created_at         TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    INDEX idx_qna_replies_qna_id (qna_id),
    CONSTRAINT fk_qna_replies_qna FOREIGN KEY (qna_id) REFERENCES qnas(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- QnaOutbox: QnA 답변 외부 동기화 아웃박스
CREATE TABLE qna_outboxes (
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    qna_id             BIGINT       NOT NULL,
    sales_channel_id   BIGINT       NOT NULL,
    external_qna_id    VARCHAR(100) NOT NULL,
    outbox_type        VARCHAR(30)  NOT NULL,
    status             VARCHAR(30)  NOT NULL DEFAULT 'PENDING',
    payload            TEXT         NOT NULL,
    retry_count        INT          NOT NULL DEFAULT 0,
    max_retry          INT          NOT NULL DEFAULT 3,
    created_at         TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at         TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    processed_at       TIMESTAMP(6),
    error_message      VARCHAR(500),
    version            BIGINT       NOT NULL DEFAULT 0,
    idempotency_key    VARCHAR(200) NOT NULL,
    INDEX idx_qna_outboxes_status_created (status, created_at),
    INDEX idx_qna_outboxes_status_updated (status, updated_at),
    UNIQUE KEY uk_qna_outboxes_idempotency (idempotency_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
