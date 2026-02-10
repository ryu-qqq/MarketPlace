package com.ryuqq.marketplace.adapter.in.rest.saleschannel.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/** 판매채널 수정 API 요청 DTO. */
@Schema(description = "판매채널 수정 요청")
public record UpdateSalesChannelApiRequest(
        @Schema(description = "판매채널명", example = "쿠팡") @NotBlank String channelName,
        @Schema(description = "상태 (ACTIVE, INACTIVE)", example = "ACTIVE")
                @NotBlank
                @Pattern(regexp = "ACTIVE|INACTIVE", message = "상태는 ACTIVE 또는 INACTIVE만 허용됩니다")
                String status) {}
