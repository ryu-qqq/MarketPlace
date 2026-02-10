package com.ryuqq.marketplace.adapter.in.rest.categorypreset.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/** 카테고리 프리셋 벌크 삭제 응답 DTO. */
@Schema(description = "카테고리 프리셋 벌크 삭제 응답")
public record DeleteCategoryPresetsApiResponse(
        @Schema(description = "삭제된 프리셋 수", example = "2") int deletedCount) {

    public static DeleteCategoryPresetsApiResponse of(int deletedCount) {
        return new DeleteCategoryPresetsApiResponse(deletedCount);
    }
}
