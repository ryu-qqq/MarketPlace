package com.ryuqq.marketplace.adapter.in.rest.sellerapplication.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * ApproveSellerApplicationApiResponse - 셀러 입점 신청 승인 응답 DTO.
 *
 * <p>API-DTO-001: Record 타입 필수.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "셀러 입점 신청 승인 응답 DTO")
public record ApproveSellerApplicationApiResponse(
        @Schema(description = "승인된 셀러 ID", example = "100") Long sellerId) {}
