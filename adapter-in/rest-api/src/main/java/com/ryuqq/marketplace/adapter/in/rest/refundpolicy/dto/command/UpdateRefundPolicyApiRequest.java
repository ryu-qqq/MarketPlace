package com.ryuqq.marketplace.adapter.in.rest.refundpolicy.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * 환불정책 수정 API 요청.
 *
 * <p>API-DTO-001: API Request DTO는 Record로 정의.
 *
 * <p>API-DTO-003: Validation 어노테이션은 API Request에만 적용.
 *
 * <p>API-DTO-004: Update Request에 ID 포함 금지 -> PathVariable에서 전달.
 *
 * @param policyName 정책명
 * @param defaultPolicy 기본 정책 여부
 * @param returnPeriodDays 반품 가능 기간 (일)
 * @param exchangePeriodDays 교환 가능 기간 (일)
 * @param nonReturnableConditions 반품 불가 조건 목록
 * @param partialRefundEnabled 부분 환불 허용 여부
 * @param inspectionRequired 검수 필요 여부
 * @param inspectionPeriodDays 검수 소요 기간 (일)
 * @param additionalInfo 추가 안내 문구
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "환불정책 수정 요청")
public record UpdateRefundPolicyApiRequest(
        @Schema(
                        description = "정책명",
                        example = "기본 환불정책",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotBlank(message = "정책명은 필수입니다")
                @Size(min = 1, max = 100, message = "정책명은 1~100자여야 합니다")
                String policyName,
        @Schema(
                        description = "기본 정책 여부",
                        example = "true",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotNull(message = "기본 정책 여부는 필수입니다")
                Boolean defaultPolicy,
        @Schema(
                        description = "반품 가능 기간 (1~90일)",
                        example = "7",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotNull(message = "반품 가능 기간은 필수입니다")
                @Min(value = 1, message = "반품 가능 기간은 1일 이상이어야 합니다")
                @Max(value = 90, message = "반품 가능 기간은 90일 이하여야 합니다")
                Integer returnPeriodDays,
        @Schema(
                        description = "교환 가능 기간 (1~90일)",
                        example = "7",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotNull(message = "교환 가능 기간은 필수입니다")
                @Min(value = 1, message = "교환 가능 기간은 1일 이상이어야 합니다")
                @Max(value = 90, message = "교환 가능 기간은 90일 이하여야 합니다")
                Integer exchangePeriodDays,
        @Schema(
                        description =
                                "반품 불가 조건 목록 (OPENED_PACKAGING, USED_PRODUCT, TIME_EXPIRED,"
                                    + " DIGITAL_CONTENT, CUSTOM_MADE, HYGIENE_PRODUCT, PARTIAL_SET,"
                                    + " MISSING_TAG, DAMAGED_BY_CUSTOMER)",
                        example = "[\"OPENED_PACKAGING\", \"USED_PRODUCT\", \"MISSING_TAG\"]")
                List<String> nonReturnableConditions,
        @Schema(description = "부분 환불 허용 여부", example = "true") Boolean partialRefundEnabled,
        @Schema(description = "검수 필요 여부", example = "true") Boolean inspectionRequired,
        @Schema(description = "검수 소요 기간 (일)", example = "3")
                @Min(value = 0, message = "검수 소요 기간은 0일 이상이어야 합니다")
                Integer inspectionPeriodDays,
        @Schema(description = "추가 안내 문구", example = "교환/반품 시 상품 택이 제거되지 않은 상태여야 합니다.")
                @Size(max = 1000, message = "추가 안내 문구는 1000자 이하여야 합니다")
                String additionalInfo) {}
