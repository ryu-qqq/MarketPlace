package com.ryuqq.marketplace.adapter.in.rest.outboundproduct.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/** 연동 재처리 응답 (API 4). */
@Schema(description = "연동 재처리 응답")
public record RetrySyncApiResponse(
        @Schema(description = "재처리 대상 Outbox ID", example = "202") long outboxId,
        @Schema(description = "상태", example = "ACCEPTED") String status) {

    public static RetrySyncApiResponse of(long outboxId) {
        return new RetrySyncApiResponse(outboxId, "ACCEPTED");
    }
}
