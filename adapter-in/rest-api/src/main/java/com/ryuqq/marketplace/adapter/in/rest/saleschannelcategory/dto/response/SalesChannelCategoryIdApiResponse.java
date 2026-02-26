package com.ryuqq.marketplace.adapter.in.rest.saleschannelcategory.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/** 외부채널 카테고리 ID 응답 DTO. */
@Schema(description = "외부채널 카테고리 ID 응답")
public record SalesChannelCategoryIdApiResponse(
        @Schema(description = "카테고리 ID 목록", example = "[100]") List<Long> categoryIds) {

    public static SalesChannelCategoryIdApiResponse of(Long categoryId) {
        return new SalesChannelCategoryIdApiResponse(List.of(categoryId));
    }

    public static SalesChannelCategoryIdApiResponse of(List<Long> categoryIds) {
        return new SalesChannelCategoryIdApiResponse(categoryIds);
    }
}
