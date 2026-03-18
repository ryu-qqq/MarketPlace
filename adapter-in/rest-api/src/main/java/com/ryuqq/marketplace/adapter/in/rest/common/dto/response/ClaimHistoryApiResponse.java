package com.ryuqq.marketplace.adapter.in.rest.common.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/** 클레임 이력 API 응답. */
@Schema(description = "클레임 이력")
public record ClaimHistoryApiResponse(
        @Schema(description = "이력 ID") String historyId,
        @Schema(description = "이력 유형") String type,
        @Schema(description = "제목") String title,
        @Schema(description = "내용") String message,
        @Schema(description = "처리자") ActorApiResponse actor,
        @Schema(description = "생성일시") String createdAt) {

    @Schema(description = "처리자 정보")
    public record ActorApiResponse(
            @Schema(description = "처리자 유형") String actorType,
            @Schema(description = "처리자 ID") String actorId,
            @Schema(description = "처리자 이름") String actorName) {}
}
