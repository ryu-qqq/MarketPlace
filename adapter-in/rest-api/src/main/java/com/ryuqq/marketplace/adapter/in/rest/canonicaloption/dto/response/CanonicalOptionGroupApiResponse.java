package com.ryuqq.marketplace.adapter.in.rest.canonicaloption.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/** 캐노니컬 옵션 그룹 API 응답 DTO. */
@Schema(description = "캐노니컬 옵션 그룹 응답")
public record CanonicalOptionGroupApiResponse(
        @Schema(description = "그룹 ID", example = "1") Long id,
        @Schema(description = "그룹 코드", example = "COLOR") String code,
        @Schema(description = "한글명", example = "색상") String nameKo,
        @Schema(description = "영문명", example = "Color") String nameEn,
        @Schema(description = "활성 상태", example = "true") boolean active,
        @Schema(description = "옵션 값 목록") List<CanonicalOptionValueApiResponse> values,
        @Schema(description = "생성일시 (KST)", example = "2026-01-15T09:30:00Z")
                String createdAt) {}
