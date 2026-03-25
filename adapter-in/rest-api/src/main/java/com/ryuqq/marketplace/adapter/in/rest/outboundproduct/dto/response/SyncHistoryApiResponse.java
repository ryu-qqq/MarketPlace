package com.ryuqq.marketplace.adapter.in.rest.outboundproduct.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/** 연동 이력 응답 (API 3). */
@Schema(description = "연동 이력 응답")
public record SyncHistoryApiResponse(
        @Schema(description = "이력 ID", example = "1001") long id,
        @Schema(description = "작업 ID", example = "SYNC-20251216-001") String jobId,
        @Schema(description = "쇼핑몰명", example = "스마트스토어") String shopName,
        @Schema(description = "계정 ID", example = "trexi001") String accountId,
        @Schema(description = "프리셋명", example = "식품 - 과자류 전송용") String presetName,
        @Schema(description = "상태", example = "SUCCESS") String status,
        @Schema(description = "상태 라벨", example = "성공") String statusLabel,
        @Schema(description = "요청일시", example = "2025-12-16 14:30:00") String requestedAt,
        @Schema(description = "완료일시", example = "2025-12-16 14:30:45") String completedAt,
        @Schema(description = "외부 상품 ID", example = "NAVER-12345678") String externalProductId,
        @Schema(description = "에러 메시지") String errorMessage,
        @Schema(description = "재시도 횟수", example = "0") int retryCount) {}
