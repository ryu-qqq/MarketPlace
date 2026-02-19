package com.ryuqq.marketplace.adapter.in.rest.notice.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/** 고시정보 필드 API 응답 DTO. */
@Schema(description = "고시정보 필드 응답")
public record NoticeFieldApiResponse(
        @Schema(description = "필드 ID", example = "1") Long id,
        @Schema(description = "필드 코드", example = "MATERIAL") String fieldCode,
        @Schema(description = "필드명", example = "소재") String fieldName,
        @Schema(description = "필수 여부", example = "true") boolean required,
        @Schema(description = "정렬 순서", example = "0") int sortOrder) {}
