package com.ryuqq.marketplace.adapter.in.rest.saleschannelbrand.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/** 외부채널 브랜드 등록 API 요청 DTO. */
@Schema(description = "외부채널 브랜드 등록 요청")
public record RegisterSalesChannelBrandApiRequest(
        @Schema(description = "외부 브랜드 코드", example = "BRD001") @NotBlank String externalBrandCode,
        @Schema(description = "외부 브랜드명", example = "나이키") @NotBlank String externalBrandName) {}
