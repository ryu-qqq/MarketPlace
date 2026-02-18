package com.ryuqq.marketplace.adapter.in.rest.product.dto.command;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 상품(SKU) + 옵션 일괄 수정 API 요청.
 *
 * <p>API-DTO-001: API Request DTO는 Record로 정의.
 *
 * <p>API-DTO-003: Validation 어노테이션은 API Request에만 적용.
 *
 * @param optionGroups 옵션 그룹 수정 데이터 목록
 * @param products 수정할 상품 데이터 목록
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "상품 + 옵션 일괄 수정 요청")
public record UpdateProductsApiRequest(
        @Schema(description = "옵션 그룹 목록", requiredMode = Schema.RequiredMode.REQUIRED)
                @NotEmpty(message = "옵션 그룹 목록은 필수입니다")
                @Valid
                List<OptionGroupApiRequest> optionGroups,
        @Schema(description = "수정할 상품 목록", requiredMode = Schema.RequiredMode.REQUIRED)
                @NotEmpty(message = "수정할 상품 목록은 필수입니다")
                @Valid
                List<ProductDataApiRequest> products) {

    /** 옵션 그룹 API Request. */
    @Schema(description = "옵션 그룹 수정 데이터")
    public record OptionGroupApiRequest(
            @Schema(description = "기존 옵션 그룹 ID (신규이면 null)") @JsonProperty("sellerOptionGroupId")
                    Long sellerOptionGroupId,
            @Schema(
                            description = "옵션 그룹명",
                            example = "색상",
                            requiredMode = Schema.RequiredMode.REQUIRED)
                    @NotBlank(message = "옵션 그룹명은 필수입니다")
                    String optionGroupName,
            @Schema(description = "표준 옵션 그룹 ID") @JsonProperty("canonicalOptionGroupId")
                    Long canonicalOptionGroupId,
            @Schema(description = "옵션 값 목록", requiredMode = Schema.RequiredMode.REQUIRED)
                    @NotEmpty(message = "옵션 값은 최소 1개 이상 필요합니다")
                    @Valid
                    List<OptionValueApiRequest> optionValues) {}

    /** 옵션 값 API Request. */
    @Schema(description = "옵션 값 수정 데이터")
    public record OptionValueApiRequest(
            @Schema(description = "기존 옵션 값 ID (신규이면 null)") @JsonProperty("sellerOptionValueId")
                    Long sellerOptionValueId,
            @Schema(
                            description = "옵션 값명",
                            example = "빨강",
                            requiredMode = Schema.RequiredMode.REQUIRED)
                    @NotBlank(message = "옵션 값명은 필수입니다")
                    String optionValueName,
            @Schema(description = "표준 옵션 값 ID") @JsonProperty("canonicalOptionValueId")
                    Long canonicalOptionValueId,
            @Schema(
                            description = "정렬 순서",
                            example = "0",
                            requiredMode = Schema.RequiredMode.REQUIRED)
                    @NotNull(message = "정렬 순서는 필수입니다")
                    @Min(value = 0, message = "정렬 순서는 0 이상이어야 합니다")
                    Integer sortOrder) {}

    /**
     * 개별 상품 수정 데이터.
     *
     * @param productId 수정 대상 상품 ID (신규이면 null)
     * @param skuCode SKU 코드
     * @param regularPrice 정가
     * @param currentPrice 판매가
     * @param stockQuantity 재고 수량
     * @param sortOrder 정렬 순서
     * @param optionIndices orderedActiveValueIds에 대한 인덱스 목록
     */
    @Schema(description = "개별 상품 수정 데이터")
    public record ProductDataApiRequest(
            @Schema(description = "상품 ID (신규이면 null)", example = "1") @JsonProperty("productId")
                    Long productId,
            @Schema(
                            description = "SKU 코드",
                            example = "SKU-001",
                            requiredMode = Schema.RequiredMode.REQUIRED)
                    @NotBlank(message = "SKU 코드는 필수입니다")
                    String skuCode,
            @Schema(
                            description = "정가",
                            example = "100000",
                            requiredMode = Schema.RequiredMode.REQUIRED)
                    @NotNull(message = "정가는 필수입니다")
                    Integer regularPrice,
            @Schema(
                            description = "판매가 (정가 이하)",
                            example = "90000",
                            requiredMode = Schema.RequiredMode.REQUIRED)
                    @NotNull(message = "판매가는 필수입니다")
                    Integer currentPrice,
            @Schema(
                            description = "재고 수량 (0 이상)",
                            example = "100",
                            requiredMode = Schema.RequiredMode.REQUIRED)
                    @NotNull(message = "재고 수량은 필수입니다")
                    @Min(value = 0, message = "재고 수량은 0 이상이어야 합니다")
                    Integer stockQuantity,
            @Schema(
                            description = "정렬 순서",
                            example = "1",
                            requiredMode = Schema.RequiredMode.REQUIRED)
                    @NotNull(message = "정렬 순서는 필수입니다")
                    @Min(value = 0, message = "정렬 순서는 0 이상이어야 합니다")
                    Integer sortOrder,
            @Schema(
                            description = "옵션 값 인덱스 목록",
                            example = "[0, 3]",
                            requiredMode = Schema.RequiredMode.REQUIRED)
                    @NotNull(message = "옵션 인덱스는 필수입니다")
                    List<Integer> optionIndices) {}
}
