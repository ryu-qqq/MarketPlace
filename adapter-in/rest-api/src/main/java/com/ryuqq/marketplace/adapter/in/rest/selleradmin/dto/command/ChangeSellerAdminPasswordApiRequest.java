package com.ryuqq.marketplace.adapter.in.rest.selleradmin.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * ChangeSellerAdminPasswordApiRequest - 셀러 관리자 비밀번호 변경 요청 DTO.
 *
 * <p>외부 본인인증(이메일/문자 등) 완료 후 새 비밀번호만 전달합니다. 현재 비밀번호는 사용하지 않습니다.
 *
 * @param newPassword 새 비밀번호
 * @author ryu-qqq
 * @since 1.1.0
 */
@Schema(description = "셀러 관리자 비밀번호 변경 요청 (본인인증 완료 후)")
public record ChangeSellerAdminPasswordApiRequest(
        @Schema(
                        description = "새 비밀번호",
                        example = "NewPass123!",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotBlank(message = "새 비밀번호는 필수입니다.")
                @Size(min = 8, max = 100, message = "비밀번호는 8자 이상 100자 이하여야 합니다.")
                String newPassword) {}
