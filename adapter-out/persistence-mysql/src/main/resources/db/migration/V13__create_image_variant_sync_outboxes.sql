-- 이미지 Variant 세토프 동기화 Outbox 테이블
CREATE TABLE IF NOT EXISTS `image_variant_sync_outboxes` (
    `id`               BIGINT       NOT NULL AUTO_INCREMENT,
    `source_image_id`  BIGINT       NOT NULL,
    `source_type`      VARCHAR(30)  NOT NULL,
    `status`           VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    `retry_count`      INT          NOT NULL DEFAULT 0,
    `max_retry`        INT          NOT NULL DEFAULT 3,
    `created_at`       TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    `updated_at`       TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    `processed_at`     TIMESTAMP(6) NULL,
    `error_message`    VARCHAR(1000) NULL,
    `version`          BIGINT       NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    INDEX `idx_ivso_status` (`status`),
    INDEX `idx_ivso_source_image_id` (`source_image_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
