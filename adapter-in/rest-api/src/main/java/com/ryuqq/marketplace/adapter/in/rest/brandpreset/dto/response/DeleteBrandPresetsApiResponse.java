package com.ryuqq.marketplace.adapter.in.rest.brandpreset.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/** 브랜드 프리셋 벌크 삭제 응답 DTO. */
@Schema(description = "브랜드 프리셋 벌크 삭제 응답")
public record DeleteBrandPresetsApiResponse(
        @Schema(description = "삭제된 프리셋 수", example = "2") int deletedCount) {

    public static DeleteBrandPresetsApiResponse of(int deletedCount) {
        return new DeleteBrandPresetsApiResponse(deletedCount);
    }
}
