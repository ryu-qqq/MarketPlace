package com.ryuqq.marketplace.adapter.in.rest.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 토큰 갱신 API 응답.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "토큰 갱신 응답")
public record RefreshApiResponse(
        @Schema(description = "액세스 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
                String accessToken,
        @Schema(description = "리프레시 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
                String refreshToken,
        @Schema(description = "토큰 타입", example = "Bearer") String tokenType,
        @Schema(description = "만료 시간 (초)", example = "3600") long expiresIn) {}
