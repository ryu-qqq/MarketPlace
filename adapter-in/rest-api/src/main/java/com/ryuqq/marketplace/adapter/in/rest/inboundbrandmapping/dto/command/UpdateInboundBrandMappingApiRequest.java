package com.ryuqq.marketplace.adapter.in.rest.inboundbrandmapping.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;

/** 외부 브랜드 매핑 수정 요청 DTO. */
@Schema(description = "외부 브랜드 매핑 수정 요청")
public record UpdateInboundBrandMappingApiRequest(
        @Schema(description = "외부 브랜드명", example = "나이키") String externalBrandName,
        @Schema(description = "내부 브랜드 ID", example = "1") Long internalBrandId,
        @Schema(description = "상태 (ACTIVE, INACTIVE)", example = "ACTIVE") String status) {}
