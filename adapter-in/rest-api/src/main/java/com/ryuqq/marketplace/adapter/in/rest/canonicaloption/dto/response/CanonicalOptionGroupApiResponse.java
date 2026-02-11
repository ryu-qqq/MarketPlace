package com.ryuqq.marketplace.adapter.in.rest.canonicaloption.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/** 캐노니컬 옵션 그룹 API 응답 DTO. */
@Schema(description = "캐노니컬 옵션 그룹 응답")
public record CanonicalOptionGroupApiResponse(
        @Schema(description = "그룹 ID") Long id,
        @Schema(description = "그룹 코드") String code,
        @Schema(description = "한글명") String nameKo,
        @Schema(description = "영문명") String nameEn,
        @Schema(description = "활성 상태") boolean active,
        @Schema(description = "옵션 값 목록") List<CanonicalOptionValueApiResponse> values,
        @Schema(description = "생성일시") String createdAt) {}
