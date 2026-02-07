package com.ryuqq.marketplace.adapter.in.rest.shippingpolicy.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 배송정책 수정 API 요청.
 *
 * <p>API-DTO-001: API Request DTO는 Record로 정의.
 *
 * <p>API-DTO-003: Validation 어노테이션은 API Request에만 적용.
 *
 * <p>API-DTO-004: Update Request에 ID 포함 금지 -> PathVariable에서 전달.
 *
 * @param policyName 정책명
 * @param defaultPolicy 기본 정책 여부
 * @param shippingFeeType 배송비 유형
 * @param baseFee 기본 배송비
 * @param freeThreshold 무료배송 기준금액
 * @param jejuExtraFee 제주 추가 배송비
 * @param islandExtraFee 도서산간 추가 배송비
 * @param returnFee 반품 배송비
 * @param exchangeFee 교환 배송비
 * @param leadTime 발송 소요일 정보
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "배송정책 수정 요청")
public record UpdateShippingPolicyApiRequest(
        @Schema(
                        description = "정책명",
                        example = "기본 배송정책",
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
                        description = "배송비 유형 (FREE, PAID, CONDITIONAL_FREE, QUANTITY_BASED)",
                        example = "CONDITIONAL_FREE",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotBlank(message = "배송비 유형은 필수입니다")
                String shippingFeeType,
        @Schema(description = "기본 배송비 (원)", example = "3000")
                @Min(value = 0, message = "기본 배송비는 0 이상이어야 합니다")
                Long baseFee,
        @Schema(description = "무료배송 기준금액 (원) - CONDITIONAL_FREE 시 필수", example = "50000")
                @Min(value = 0, message = "무료배송 기준금액은 0 이상이어야 합니다")
                Long freeThreshold,
        @Schema(description = "제주 추가 배송비 (원)", example = "3000")
                @Min(value = 0, message = "제주 추가 배송비는 0 이상이어야 합니다")
                Long jejuExtraFee,
        @Schema(description = "도서산간 추가 배송비 (원)", example = "5000")
                @Min(value = 0, message = "도서산간 추가 배송비는 0 이상이어야 합니다")
                Long islandExtraFee,
        @Schema(description = "반품 배송비 (원)", example = "3000")
                @Min(value = 0, message = "반품 배송비는 0 이상이어야 합니다")
                Long returnFee,
        @Schema(description = "교환 배송비 (원)", example = "6000")
                @Min(value = 0, message = "교환 배송비는 0 이상이어야 합니다")
                Long exchangeFee,
        @Schema(description = "발송 소요일 정보") @Valid
                RegisterShippingPolicyApiRequest.LeadTimeApiRequest leadTime) {}
