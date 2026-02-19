package com.ryuqq.marketplace.adapter.in.rest.externalsource.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;

/** 외부 소스 수정 요청 DTO. */
@Schema(description = "외부 소스 수정 요청")
public record UpdateExternalSourceApiRequest(
        @Schema(description = "외부 소스명", example = "네이버 커머스") String name,
        @Schema(description = "상태 (ACTIVE, INACTIVE)", example = "ACTIVE") String status,
        @Schema(description = "설명", example = "네이버 커머스 연동") String description) {}
