package com.ryuqq.marketplace.adapter.in.rest.selleradmin.dto.response;

import com.ryuqq.marketplace.application.selleradmin.dto.response.VerifySellerAdminResult;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 셀러 관리자 본인 확인 API 응답.
 *
 * @param exists 존재 여부
 * @param status 상태 (존재하지 않으면 null)
 * @author ryu-qqq
 * @since 1.1.0
 */
@Schema(description = "셀러 관리자 본인 확인 응답")
public record VerifySellerAdminApiResponse(
        @Schema(description = "존재 여부", example = "true") boolean exists,
        @Schema(description = "상태 (PENDING_APPROVAL, ACTIVE, REJECTED 등)", example = "ACTIVE")
                String status) {

    public static VerifySellerAdminApiResponse from(VerifySellerAdminResult result) {
        return new VerifySellerAdminApiResponse(result.exists(), result.status());
    }
}
