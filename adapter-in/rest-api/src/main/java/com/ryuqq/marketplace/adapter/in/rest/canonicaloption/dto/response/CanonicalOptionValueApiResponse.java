package com.ryuqq.marketplace.adapter.in.rest.canonicaloption.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/** 캐노니컬 옵션 값 API 응답 DTO. */
@Schema(description = "캐노니컬 옵션 값 응답")
public record CanonicalOptionValueApiResponse(
        @Schema(description = "옵션 값 ID", example = "10") Long id,
        @Schema(description = "옵션 값 코드", example = "RED") String code,
        @Schema(description = "한글명", example = "빨강") String nameKo,
        @Schema(description = "영문명", example = "Red") String nameEn,
        @Schema(description = "정렬 순서", example = "1") int sortOrder) {}
