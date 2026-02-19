package com.ryuqq.marketplace.adapter.in.rest.externalbrandmapping.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/** 외부 브랜드 매핑 조회 응답 DTO. */
@Schema(description = "외부 브랜드 매핑 조회 응답")
public record ExternalBrandMappingApiResponse(
        @Schema(description = "매핑 ID") Long id,
        @Schema(description = "외부 소스 ID") Long externalSourceId,
        @Schema(description = "외부 브랜드 코드") String externalBrandCode,
        @Schema(description = "외부 브랜드명") String externalBrandName,
        @Schema(description = "내부 브랜드 ID") Long internalBrandId,
        @Schema(description = "상태") String status,
        @Schema(description = "생성일시") String createdAt,
        @Schema(description = "수정일시") String updatedAt) {}
