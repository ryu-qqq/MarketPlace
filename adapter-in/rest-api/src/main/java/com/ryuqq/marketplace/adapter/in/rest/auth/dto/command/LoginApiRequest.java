package com.ryuqq.marketplace.adapter.in.rest.auth.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 로그인 API 요청.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "로그인 요청")
public record LoginApiRequest(
        @Schema(
                        description = "사용자 식별자 (이메일 또는 아이디)",
                        example = "admin@example.com",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotBlank(message = "식별자는 필수입니다")
                @Size(max = 100, message = "식별자는 100자 이하여야 합니다")
                String identifier,
        @Schema(
                        description = "비밀번호",
                        example = "password123!",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotBlank(message = "비밀번호는 필수입니다")
                @Size(max = 100, message = "비밀번호는 100자 이하여야 합니다")
                String password) {}
