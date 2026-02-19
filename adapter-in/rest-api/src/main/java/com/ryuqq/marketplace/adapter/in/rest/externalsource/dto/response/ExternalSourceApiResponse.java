package com.ryuqq.marketplace.adapter.in.rest.externalsource.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/** 외부 소스 조회 응답 DTO. */
@Schema(description = "외부 소스 조회 응답")
public record ExternalSourceApiResponse(
        @Schema(description = "외부 소스 ID", example = "1") Long id,
        @Schema(description = "외부 소스 코드", example = "NAVER_COMMERCE") String code,
        @Schema(description = "외부 소스명", example = "네이버 커머스") String name,
        @Schema(description = "유형 (CRAWLING, LEGACY, PARTNER)", example = "CRAWLING") String type,
        @Schema(description = "상태 (ACTIVE, INACTIVE)", example = "ACTIVE") String status,
        @Schema(description = "설명", example = "네이버 커머스 연동") String description,
        @Schema(description = "생성일시 (ISO 8601)", example = "2025-01-23T10:30:00+09:00")
                String createdAt,
        @Schema(description = "수정일시 (ISO 8601)", example = "2025-01-23T10:30:00+09:00")
                String updatedAt) {}
