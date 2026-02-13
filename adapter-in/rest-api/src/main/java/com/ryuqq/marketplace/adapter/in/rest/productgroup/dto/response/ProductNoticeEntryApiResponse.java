package com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/** 고시정보 항목 API 응답 DTO. */
@Schema(description = "고시정보 항목 응답")
public record ProductNoticeEntryApiResponse(
        @Schema(description = "항목 ID") Long id,
        @Schema(description = "고시정보 필드 ID") Long noticeFieldId,
        @Schema(description = "필드 값") String fieldValue) {}
