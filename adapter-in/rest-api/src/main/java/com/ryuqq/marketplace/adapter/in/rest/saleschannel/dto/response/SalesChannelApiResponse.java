package com.ryuqq.marketplace.adapter.in.rest.saleschannel.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/** 판매채널 조회 응답 DTO. */
@Schema(description = "판매채널 응답")
public record SalesChannelApiResponse(
        @Schema(description = "판매채널 ID", example = "1") Long id,
        @Schema(description = "판매채널명", example = "쿠팡") String channelName,
        @Schema(description = "상태", example = "ACTIVE") String status,
        @Schema(description = "생성일시", example = "2025-01-23T10:30:00+09:00") String createdAt,
        @Schema(description = "수정일시", example = "2025-01-23T10:30:00+09:00") String updatedAt) {}
