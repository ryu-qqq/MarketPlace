package com.ryuqq.marketplace.adapter.in.rest.categorypreset.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/** 카테고리 프리셋 수정 API 요청 DTO. */
@Schema(description = "카테고리 프리셋 수정 요청")
public record UpdateCategoryPresetApiRequest(
        @Schema(description = "프리셋 이름", example = "수정된 프리셋 이름") String presetName,
        @Schema(description = "외부 카테고리 코드", example = "50000456") String categoryCode,
        @Schema(description = "매핑할 내부 카테고리 ID 목록", example = "[1, 2, 3]")
                List<Long> internalCategoryIds) {}
