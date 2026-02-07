package com.ryuqq.marketplace.adapter.in.rest.refundpolicy.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * NonReturnableConditionApiResponse - 반품 불가 조건 API 응답.
 *
 * <p>API-DTO-001: Record 타입 필수.
 *
 * <p>API-DTO-002: DTO 불변성 보장.
 *
 * @param code 조건 코드
 * @param displayName 조건 표시명
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "반품 불가 조건 응답")
public record NonReturnableConditionApiResponse(
        @Schema(description = "조건 코드", example = "OPENED_PACKAGING") String code,
        @Schema(description = "조건 표시명", example = "포장 개봉") String displayName) {}
