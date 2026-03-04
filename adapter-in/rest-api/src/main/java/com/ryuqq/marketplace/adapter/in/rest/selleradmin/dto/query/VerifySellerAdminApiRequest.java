package com.ryuqq.marketplace.adapter.in.rest.selleradmin.dto.query;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.NotBlank;

/**
 * 셀러 관리자 본인 확인 API 요청.
 *
 * <p>이름과 로그인 ID로 셀러 관리자를 확인하고 핸드폰 번호를 반환합니다.
 *
 * @param name 관리자 이름
 * @param loginId 로그인 ID
 * @author ryu-qqq
 * @since 1.1.0
 */
public record VerifySellerAdminApiRequest(
        @Parameter(description = "관리자 이름", required = true, example = "홍길동")
                @NotBlank(message = "이름은 필수입니다")
                String name,
        @Parameter(description = "로그인 ID", required = true, example = "seller01")
                @NotBlank(message = "로그인 ID는 필수입니다")
                String loginId) {}
