package com.ryuqq.marketplace.adapter.in.rest.selleradmin.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * ApplySellerAdminApiResponse - 셀러 관리자 가입 신청 생성 응답 DTO.
 *
 * <p>API-DTO-001: Record 타입 필수.
 *
 * @param sellerAdminId 생성된 셀러 관리자 ID (UUIDv7)
 * @author ryu-qqq
 * @since 1.1.0
 */
@Schema(description = "셀러 관리자 가입 신청 생성 응답 DTO")
public record ApplySellerAdminApiResponse(
        @Schema(description = "생성된 셀러 관리자 ID", example = "01956f4a-2b3c-7d8e-9f0a-1b2c3d4e5f6g")
                String sellerAdminId) {}
