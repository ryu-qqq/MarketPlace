package com.ryuqq.marketplace.adapter.in.rest.inboundbrandmapping.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/** 외부 브랜드 매핑 조회 응답 DTO. */
@Schema(description = "외부 브랜드 매핑 조회 응답")
public record InboundBrandMappingApiResponse(
        @Schema(description = "매핑 ID", example = "1") Long id,
        @Schema(description = "외부 소스 ID", example = "1") Long inboundSourceId,
        @Schema(description = "외부 브랜드 코드", example = "NV_BRAND_001") String externalBrandCode,
        @Schema(description = "외부 브랜드명", example = "나이키") String externalBrandName,
        @Schema(description = "내부 브랜드 ID", example = "1") Long internalBrandId,
        @Schema(description = "상태 (ACTIVE, INACTIVE)", example = "ACTIVE") String status,
        @Schema(description = "생성일시 (KST)", example = "2025-01-23 10:30:00")
                String createdAt,
        @Schema(description = "수정일시 (KST)", example = "2025-01-23 10:30:00")
                String updatedAt) {}
