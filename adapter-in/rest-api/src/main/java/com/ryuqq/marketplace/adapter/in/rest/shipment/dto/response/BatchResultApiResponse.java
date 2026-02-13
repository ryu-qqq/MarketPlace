package com.ryuqq.marketplace.adapter.in.rest.shipment.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/** 일괄 처리 결과 응답 DTO. */
@Schema(description = "일괄 처리 결과 응답")
public record BatchResultApiResponse(
        @Schema(description = "총 처리 건수") int totalCount,
        @Schema(description = "성공 건수") int successCount,
        @Schema(description = "실패 건수") int failureCount,
        @Schema(description = "개별 항목 결과 목록") List<BatchResultItemApiResponse> results) {

    /** 일괄 처리 개별 항목 결과. */
    @Schema(description = "일괄 처리 개별 항목 결과")
    public record BatchResultItemApiResponse(
            @Schema(description = "처리 대상 ID") String id,
            @Schema(description = "성공 여부") boolean success,
            @Schema(description = "에러 코드") String errorCode,
            @Schema(description = "에러 메시지") String errorMessage) {}
}
