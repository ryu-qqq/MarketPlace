package com.ryuqq.marketplace.adapter.in.rest.sellerapplication.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * ApplySellerApplicationApiResponse - 셀러 입점 신청 등록 응답 DTO.
 *
 * <p>API-DTO-001: Record 타입 필수.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "셀러 입점 신청 등록 응답 DTO")
public record ApplySellerApplicationApiResponse(
        @Schema(description = "생성된 신청 ID", example = "1") Long applicationId) {}
