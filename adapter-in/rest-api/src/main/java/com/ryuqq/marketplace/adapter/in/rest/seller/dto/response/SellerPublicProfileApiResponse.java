package com.ryuqq.marketplace.adapter.in.rest.seller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * SellerPublicProfileApiResponse - 셀러 공개 프로필 응답 DTO.
 *
 * <p>인증 없이 조회 가능한 셀러 간소화 프로필 응답.
 *
 * <p>API-DTO-001: Record 타입 필수.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "셀러 공개 프로필 응답 DTO")
public record SellerPublicProfileApiResponse(
        @Schema(description = "셀러명", example = "테스트셀러") String sellerName,
        @Schema(description = "표시명", example = "테스트 브랜드") String displayName,
        @Schema(description = "회사명", example = "테스트컴퍼니") String companyName,
        @Schema(description = "대표자명", example = "홍길동") String representative) {}
