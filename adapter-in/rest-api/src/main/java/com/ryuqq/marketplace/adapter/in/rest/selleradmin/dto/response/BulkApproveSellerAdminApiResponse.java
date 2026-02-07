package com.ryuqq.marketplace.adapter.in.rest.selleradmin.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * BulkApproveSellerAdminApiResponse - 셀러 관리자 가입 신청 일괄 승인 응답 DTO.
 *
 * @param totalCount 총 처리 건수
 * @param successCount 성공 건수
 * @param failureCount 실패 건수
 * @param results 개별 항목 결과 목록
 * @author ryu-qqq
 * @since 1.1.0
 */
@Schema(description = "셀러 관리자 가입 신청 일괄 승인 응답 DTO")
public record BulkApproveSellerAdminApiResponse(
        @Schema(description = "총 처리 건수") int totalCount,
        @Schema(description = "성공 건수") int successCount,
        @Schema(description = "실패 건수") int failureCount,
        @Schema(description = "개별 항목 결과 목록") List<ItemResult> results) {

    @Schema(description = "개별 승인 처리 결과")
    public record ItemResult(
            @Schema(description = "셀러 관리자 ID") String sellerAdminId,
            @Schema(description = "성공 여부") boolean success,
            @Schema(description = "에러 코드 (실패 시)") String errorCode,
            @Schema(description = "에러 메시지 (실패 시)") String errorMessage) {}
}
