package com.ryuqq.marketplace.adapter.in.rest.auth.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * 토큰 갱신 API 요청.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "토큰 갱신 요청")
public record RefreshApiRequest(
        @Schema(
                        description = "리프레시 토큰",
                        example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotBlank(message = "리프레시 토큰은 필수입니다")
                String refreshToken) {}
