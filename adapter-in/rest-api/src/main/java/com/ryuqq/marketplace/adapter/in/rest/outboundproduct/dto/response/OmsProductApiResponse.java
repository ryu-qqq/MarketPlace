package com.ryuqq.marketplace.adapter.in.rest.outboundproduct.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/** OMS 상품 목록 응답 (API 1). */
@Schema(description = "OMS 상품 목록 응답")
public record OmsProductApiResponse(
        @Schema(description = "상품그룹 ID", example = "1") long id,
        @Schema(description = "상품 코드", example = "PG-1") String productCode,
        @Schema(description = "상품명", example = "나이키 에어맥스") String productName,
        @Schema(description = "대표 이미지 URL") String imageUrl,
        @Schema(description = "대표 가격", example = "89000") int price,
        @Schema(description = "총 재고", example = "150") int stock,
        @Schema(description = "상품 상태", example = "ACTIVE") String status,
        @Schema(description = "상품 상태 라벨", example = "판매중") String statusLabel,
        @Schema(description = "파트너(셀러)명", example = "나이키코리아") String partnerName,
        @Schema(description = "등록일", example = "2026-01-15 10:30:00") String createdAt,
        @Schema(description = "연동 상태", example = "SUCCESS") String syncStatus,
        @Schema(description = "연동 상태 라벨", example = "연동완료") String syncStatusLabel,
        @Schema(description = "마지막 연동일", example = "2026-03-01 14:00:00") String lastSyncAt,
        @Schema(description = "샵 ID", example = "1") Long shopId,
        @Schema(description = "샵 이름", example = "trexi-naver") String shopName) {}
