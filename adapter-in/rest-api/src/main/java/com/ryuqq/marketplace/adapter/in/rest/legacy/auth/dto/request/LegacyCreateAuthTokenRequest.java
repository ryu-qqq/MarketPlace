package com.ryuqq.marketplace.adapter.in.rest.legacy.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** 세토프 CreateAuthToken 호환 요청 DTO. */
public record LegacyCreateAuthTokenRequest(
        @NotNull(message = "userId는 필수입니다.") @NotBlank(message = "userId는 비어있을 수 없습니다.")
                String userId,
        @NotNull(message = "password는 필수입니다.") @NotBlank(message = "password는 비어있을 수 없습니다.")
                String password,
        @NotNull(message = "roleType은 필수입니다.") @NotBlank(message = "roleType은 비어있을 수 없습니다.")
                String roleType) {}
