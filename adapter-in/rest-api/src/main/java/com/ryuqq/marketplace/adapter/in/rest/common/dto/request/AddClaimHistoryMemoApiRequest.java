package com.ryuqq.marketplace.adapter.in.rest.common.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/** 수기 메모 등록 요청. */
@Schema(description = "수기 메모 등록 요청")
public record AddClaimHistoryMemoApiRequest(
        @Schema(description = "메모 내용") @NotBlank String message) {}
