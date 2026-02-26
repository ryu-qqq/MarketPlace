package com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * 상품 그룹 배치 등록 API 요청.
 *
 * <p>API-DTO-001: API Request DTO는 Record로 정의.
 *
 * <p>API-DTO-003: Validation 어노테이션은 API Request에만 적용.
 *
 * @param items 등록할 상품 그룹 목록 (최대 100건)
 * @author ryu-qqq
 * @since 1.1.0
 */
@Schema(description = "상품 그룹 배치 등록 요청")
public record BatchRegisterProductGroupApiRequest(
        @Schema(description = "등록할 상품 그룹 목록 (최대 100건)", requiredMode = Schema.RequiredMode.REQUIRED)
                @NotEmpty(message = "등록할 상품 그룹 목록은 필수입니다")
                @Size(max = 100, message = "한 번에 최대 100건까지 등록할 수 있습니다")
                @Valid
                List<RegisterProductGroupExcelApiRequest> items) {}
