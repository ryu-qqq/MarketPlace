-- 아웃바운드 상품 이미지 테이블 (채널별 외부 URL 캐싱)
CREATE TABLE `outbound_product_images` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `outbound_product_id` bigint NOT NULL COMMENT '아웃바운드 상품 ID',
  `product_group_image_id` bigint DEFAULT NULL COMMENT '상품 그룹 이미지 ID (nullable)',
  `origin_url` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '원본 이미지 URL (S3)',
  `external_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '외부 채널 CDN URL (pstatic.net 등)',
  `image_type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '이미지 타입 (THUMBNAIL, DETAIL)',
  `sort_order` int NOT NULL DEFAULT '0' COMMENT '정렬 순서',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '삭제 여부',
  `deleted_at` datetime(6) DEFAULT NULL COMMENT '삭제 일시',
  `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '생성일시',
  `updated_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '갱신일시',
  PRIMARY KEY (`id`),
  KEY `idx_outbound_product_images_outbound_product_id` (`outbound_product_id`),
  KEY `idx_outbound_product_images_outbound_product_id_deleted` (`outbound_product_id`, `deleted`),
  KEY `idx_outbound_product_images_product_group_image_id` (`product_group_image_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='아웃바운드 상품 이미지 (채널별 외부 URL 캐싱)';
