package com.ryuqq.marketplace.adapter.in.rest.shipment.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/** 일괄 처리 결과 응답 DTO. */
@Schema(description = "일괄 처리 결과 응답")
public record BatchResultApiResponse(
        @Schema(description = "총 처리 건수", example = "5") int totalCount,
        @Schema(description = "성공 건수", example = "4") int successCount,
        @Schema(description = "실패 건수", example = "1") int failureCount,
        @Schema(description = "개별 항목 결과 목록") List<BatchResultItemApiResponse> results) {

    /** 일괄 처리 개별 항목 결과. */
    @Schema(description = "일괄 처리 개별 항목 결과")
    public record BatchResultItemApiResponse(
            @Schema(description = "처리 대상 ID", example = "ship-001") String id,
            @Schema(description = "성공 여부", example = "true") boolean success,
            @Schema(description = "에러 코드", example = "INVALID_STATUS") String errorCode,
            @Schema(description = "에러 메시지", example = "이미 발송 완료된 배송입니다.") String errorMessage) {}
}
