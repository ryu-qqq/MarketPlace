-- Order 상태 제거 리팩토링: order_histories 삭제 + order_item_histories 생성
-- orders.status 컬럼은 하위 호환성을 위해 유지하되 애플리케이션에서 사용하지 않음

-- 1. order_histories 테이블 삭제
DROP TABLE IF EXISTS `order_histories`;

-- 2. order_item_histories 테이블 생성
CREATE TABLE `order_item_histories` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_item_id` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '주문상품 ID (FK)',
  `from_status` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '이전 상태',
  `to_status` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '변경된 상태',
  `changed_by` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '변경자',
  `reason` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '변경 사유',
  `changed_at` timestamp NOT NULL COMMENT '변경 시각',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
  PRIMARY KEY (`id`),
  KEY `idx_order_item_histories_order_item_id` (`order_item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
