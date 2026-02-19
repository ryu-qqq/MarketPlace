package com.ryuqq.marketplace.adapter.in.rest.externalsource.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/** 외부 소스 ID 응답 DTO. */
@Schema(description = "외부 소스 ID 응답")
public record ExternalSourceIdApiResponse(
        @Schema(description = "외부 소스 ID", example = "1") Long externalSourceId) {

    public static ExternalSourceIdApiResponse of(Long externalSourceId) {
        return new ExternalSourceIdApiResponse(externalSourceId);
    }
}
