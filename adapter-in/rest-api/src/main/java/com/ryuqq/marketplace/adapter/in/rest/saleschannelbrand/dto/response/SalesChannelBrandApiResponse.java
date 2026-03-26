package com.ryuqq.marketplace.adapter.in.rest.saleschannelbrand.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/** 외부채널 브랜드 조회 응답 DTO. */
@Schema(description = "외부채널 브랜드 응답")
public record SalesChannelBrandApiResponse(
        @Schema(description = "브랜드 ID", example = "1") Long id,
        @Schema(description = "판매채널 ID", example = "1") Long salesChannelId,
        @Schema(description = "외부 브랜드 코드", example = "BRD001") String externalBrandCode,
        @Schema(description = "외부 브랜드명", example = "나이키") String externalBrandName,
        @Schema(description = "상태 (ACTIVE, INACTIVE)", example = "ACTIVE") String status,
        @Schema(description = "생성일시", example = "2025-01-23 10:30:00") String createdAt,
        @Schema(description = "수정일시", example = "2025-01-23 10:30:00") String updatedAt) {}
