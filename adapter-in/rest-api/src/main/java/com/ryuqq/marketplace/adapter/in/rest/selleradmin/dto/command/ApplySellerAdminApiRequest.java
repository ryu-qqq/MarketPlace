package com.ryuqq.marketplace.adapter.in.rest.selleradmin.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * ApplySellerAdminApiRequest - 셀러 관리자 가입 신청 요청 DTO.
 *
 * <p>API-DTO-001: Record 타입 필수.
 *
 * <p>API-DTO-002: Jakarta Validation 필수.
 *
 * @param sellerId 가입할 셀러 ID
 * @param loginId 로그인 ID (이메일 형식)
 * @param name 관리자 이름
 * @param phoneNumber 휴대폰 번호
 * @param password 비밀번호
 * @author ryu-qqq
 * @since 1.1.0
 */
@Schema(description = "셀러 관리자 가입 신청 요청 DTO")
public record ApplySellerAdminApiRequest(
        @Schema(description = "가입할 셀러 ID", example = "1")
                @jakarta.validation.constraints.NotNull(message = "셀러 ID는 필수입니다.")
                Long sellerId,
        @Schema(description = "로그인 ID (이메일 형식)", example = "admin@example.com")
                @NotBlank(message = "로그인 ID는 필수입니다.")
                @Email(message = "올바른 이메일 형식이 아닙니다.")
                @Size(max = 100, message = "로그인 ID는 100자 이하여야 합니다.")
                String loginId,
        @Schema(description = "관리자 이름", example = "홍길동")
                @NotBlank(message = "이름은 필수입니다.")
                @Size(max = 50, message = "이름은 50자 이하여야 합니다.")
                String name,
        @Schema(description = "휴대폰 번호", example = "010-1234-5678")
                @NotBlank(message = "휴대폰 번호는 필수입니다.")
                @Size(max = 20, message = "휴대폰 번호는 20자 이하여야 합니다.")
                @Pattern(regexp = "^[0-9\\-]+$", message = "휴대폰 번호는 숫자와 하이픈만 입력 가능합니다.")
                String phoneNumber,
        @Schema(description = "비밀번호", example = "Password123!")
                @NotBlank(message = "비밀번호는 필수입니다.")
                @Size(min = 8, max = 100, message = "비밀번호는 8자 이상 100자 이하여야 합니다.")
                String password) {}
