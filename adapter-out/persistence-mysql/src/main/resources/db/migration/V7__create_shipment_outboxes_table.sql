-- 배송 상태 변경 Outbox 테이블
CREATE TABLE `shipment_outboxes` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `order_item_id` bigint NOT NULL COMMENT '주문상품 ID',
  `outbox_type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '아웃박스 유형 (CONFIRM, SHIP, DELIVER, CANCEL)',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '처리 상태 (PENDING, PROCESSING, COMPLETED, FAILED)',
  `payload` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '페이로드 JSON (송장번호, 택배사 등)',
  `retry_count` int NOT NULL DEFAULT '0' COMMENT '재시도 횟수',
  `max_retry` int NOT NULL DEFAULT '3' COMMENT '최대 재시도 횟수',
  `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '생성일시',
  `updated_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '갱신일시',
  `processed_at` datetime(6) DEFAULT NULL COMMENT '처리완료일시',
  `error_message` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '에러 메시지',
  `version` bigint NOT NULL DEFAULT '0' COMMENT '낙관적 락 버전',
  `idempotency_key` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '멱등키',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_shipment_outboxes_idempotency` (`idempotency_key`),
  KEY `idx_shipment_outboxes_status_retry` (`status`,`retry_count`),
  KEY `idx_shipment_outboxes_order_item_id` (`order_item_id`),
  KEY `idx_shipment_outboxes_status_created` (`status`,`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='배송 상태 변경 Outbox';
