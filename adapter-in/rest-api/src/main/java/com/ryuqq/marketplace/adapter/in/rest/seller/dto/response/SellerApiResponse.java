package com.ryuqq.marketplace.adapter.in.rest.seller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * SellerApiResponse - 셀러 조회 응답 DTO.
 *
 * <p>API-DTO-001: Record 타입 필수.
 *
 * <p>API-DTO-004: createdAt/updatedAt 필수.
 *
 * <p>API-DTO-005: 날짜 String 변환 필수.
 *
 * <p>API-DTO-007: @Schema 어노테이션.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "셀러 조회 응답 DTO")
public record SellerApiResponse(
        @Schema(description = "셀러 ID", example = "1") Long id,
        @Schema(description = "셀러명", example = "테스트셀러") String sellerName,
        @Schema(description = "표시명", example = "테스트 브랜드") String displayName,
        @Schema(description = "로고 URL", example = "https://example.com/logo.png") String logoUrl,
        @Schema(description = "설명", example = "테스트 셀러 설명입니다.") String description,
        @Schema(description = "활성화 여부", example = "true") boolean active,
        @Schema(description = "생성일시 (ISO 8601)", example = "2025-01-23T10:30:00+09:00")
                String createdAt,
        @Schema(description = "수정일시 (ISO 8601)", example = "2025-01-23T10:30:00+09:00")
                String updatedAt) {}
