package com.ryuqq.marketplace.adapter.in.rest.externalsource.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/** 외부 소스 조회 응답 DTO. */
@Schema(description = "외부 소스 조회 응답")
public record ExternalSourceApiResponse(
        @Schema(description = "외부 소스 ID") Long id,
        @Schema(description = "외부 소스 코드") String code,
        @Schema(description = "외부 소스명") String name,
        @Schema(description = "유형") String type,
        @Schema(description = "상태") String status,
        @Schema(description = "설명") String description,
        @Schema(description = "생성일시") String createdAt,
        @Schema(description = "수정일시") String updatedAt) {}
