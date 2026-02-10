package com.ryuqq.marketplace.adapter.in.rest.saleschannelbrand.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/** 외부채널 브랜드 ID 응답 DTO. */
@Schema(description = "외부채널 브랜드 ID 응답")
public record SalesChannelBrandIdApiResponse(
        @Schema(description = "브랜드 ID 목록") List<Long> brandIds) {

    public static SalesChannelBrandIdApiResponse of(Long brandId) {
        return new SalesChannelBrandIdApiResponse(List.of(brandId));
    }

    public static SalesChannelBrandIdApiResponse of(List<Long> brandIds) {
        return new SalesChannelBrandIdApiResponse(brandIds);
    }
}
