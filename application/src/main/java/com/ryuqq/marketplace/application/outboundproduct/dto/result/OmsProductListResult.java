package com.ryuqq.marketplace.application.outboundproduct.dto.result;

import java.time.Instant;

/**
 * OMS 상품 목록용 결과 DTO (상품 기본정보 + 연동 상태).
 *
 * @param id 상품그룹 ID
 * @param productCode 상품 코드 (productGroupId 기반 생성)
 * @param productName 상품명
 * @param imageUrl 대표 이미지 URL
 * @param price 대표 가격
 * @param stock 총 재고
 * @param status 상품 상태
 * @param statusLabel 상품 상태 라벨
 * @param partnerName 파트너(셀러)명
 * @param createdAt 등록일
 * @param syncStatus 연동 상태 (SUCCESS/FAILED/PENDING/NONE)
 * @param syncStatusLabel 연동 상태 라벨
 * @param lastSyncAt 마지막 연동일
 * @param shopId 샵 ID
 * @param shopName 샵 이름
 */
public record OmsProductListResult(
        long id,
        String productCode,
        String productName,
        String imageUrl,
        int price,
        int stock,
        String status,
        String statusLabel,
        String partnerName,
        Instant createdAt,
        String syncStatus,
        String syncStatusLabel,
        Instant lastSyncAt,
        Long shopId,
        String shopName) {}
