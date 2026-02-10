package com.ryuqq.marketplace.adapter.in.rest.categorypreset.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/** 카테고리 프리셋 벌크 삭제 API 요청 DTO. */
@Schema(description = "카테고리 프리셋 벌크 삭제 요청")
public record DeleteCategoryPresetsApiRequest(
        @Schema(description = "삭제할 프리셋 ID 목록", example = "[1001, 1002]") @NotEmpty
                List<Long> ids) {}
