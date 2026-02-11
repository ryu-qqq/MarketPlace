package com.ryuqq.marketplace.adapter.in.rest.canonicaloption.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/** 캐노니컬 옵션 값 API 응답 DTO. */
@Schema(description = "캐노니컬 옵션 값 응답")
public record CanonicalOptionValueApiResponse(
        @Schema(description = "옵션 값 ID") Long id,
        @Schema(description = "옵션 값 코드") String code,
        @Schema(description = "한글명") String nameKo,
        @Schema(description = "영문명") String nameEn,
        @Schema(description = "정렬 순서") int sortOrder) {}
