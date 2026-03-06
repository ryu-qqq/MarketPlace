package com.ryuqq.marketplace.adapter.in.rest.selleradmin.dto.response;

import com.ryuqq.marketplace.application.selleradmin.dto.response.VerifySellerAdminResult;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 셀러 관리자 본인 확인 API 응답.
 *
 * @param exists 존재 여부
 * @param status 셀러 관리자 상태
 * @param sellerAdminId 셀러 관리자 ID
 * @param phoneNumber 핸드폰 번호
 * @author ryu-qqq
 * @since 1.1.0
 */
@Schema(description = "셀러 관리자 본인 확인 응답")
public record VerifySellerAdminApiResponse(
        @Schema(description = "존재 여부", example = "true") boolean exists,
        @Schema(description = "셀러 관리자 상태", example = "ACTIVE") String status,
        @Schema(description = "셀러 관리자 ID", example = "01956f4a-2b3c-7d8e-9f0a-1b2c3d4e5f60")
                String sellerAdminId,
        @Schema(description = "핸드폰 번호", example = "010-1234-5678") String phoneNumber) {

    public static VerifySellerAdminApiResponse from(VerifySellerAdminResult result) {
        return new VerifySellerAdminApiResponse(
                result.exists(), result.status(), result.sellerAdminId(), result.phoneNumber());
    }
}
