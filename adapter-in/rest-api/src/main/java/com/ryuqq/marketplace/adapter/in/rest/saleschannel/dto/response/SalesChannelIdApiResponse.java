package com.ryuqq.marketplace.adapter.in.rest.saleschannel.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/** 판매채널 ID 응답 DTO. */
@Schema(description = "판매채널 ID 응답")
public record SalesChannelIdApiResponse(
        @Schema(description = "판매채널 ID", example = "1") Long salesChannelId) {

    public static SalesChannelIdApiResponse of(Long salesChannelId) {
        return new SalesChannelIdApiResponse(salesChannelId);
    }
}
