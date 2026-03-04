package com.ryuqq.marketplace.adapter.in.rest.selleradmin.dto.query;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.NotBlank;

/**
 * 셀러 관리자 본인 확인 API 요청.
 *
 * <p>이름과 핸드폰번호로 셀러 관리자 존재 여부 및 상태를 확인합니다.
 *
 * @param name 관리자 이름
 * @param phoneNumber 핸드폰 번호
 * @author ryu-qqq
 * @since 1.1.0
 */
public record VerifySellerAdminApiRequest(
        @Parameter(description = "관리자 이름", required = true, example = "홍길동")
                @NotBlank(message = "이름은 필수입니다")
                String name,
        @Parameter(description = "핸드폰 번호", required = true, example = "01012345678")
                @NotBlank(message = "핸드폰 번호는 필수입니다")
                String phoneNumber) {}
