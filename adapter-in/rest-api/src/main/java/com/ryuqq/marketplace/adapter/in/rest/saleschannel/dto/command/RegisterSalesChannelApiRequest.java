package com.ryuqq.marketplace.adapter.in.rest.saleschannel.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/** 판매채널 등록 API 요청 DTO. */
@Schema(description = "판매채널 등록 요청")
public record RegisterSalesChannelApiRequest(
        @Schema(description = "판매채널명", example = "쿠팡") @NotBlank String channelName) {}
