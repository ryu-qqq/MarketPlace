package com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/** 고시정보 항목 API 응답 DTO. */
@Schema(description = "고시정보 항목 응답")
public record ProductNoticeEntryApiResponse(
        @Schema(description = "항목 ID", example = "1") Long id,
        @Schema(description = "고시정보 필드 ID", example = "1") Long noticeFieldId,
        @Schema(description = "필드 값", example = "면 100%") String fieldValue) {}
