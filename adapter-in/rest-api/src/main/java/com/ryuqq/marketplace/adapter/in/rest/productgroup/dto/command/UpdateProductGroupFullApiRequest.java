package com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.command;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * 상품 그룹 전체 수정 API Request.
 *
 * <p>ProductGroup + Description + Notice + Products를 한번에 수정합니다.
 */
@Schema(description = "상품 그룹 전체 수정 요청")
public record UpdateProductGroupFullApiRequest(
        @Schema(
                        description = "상품 그룹명",
                        example = "나이키 에어맥스 90",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotBlank(message = "상품 그룹명은 필수입니다")
                @Size(max = 200, message = "상품 그룹명은 200자 이내여야 합니다")
                String productGroupName,
        @Schema(description = "브랜드 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
                @NotNull(message = "브랜드 ID는 필수입니다")
                @Min(value = 1, message = "브랜드 ID는 1 이상이어야 합니다")
                Long brandId,
        @Schema(
                        description = "카테고리 ID",
                        example = "100",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotNull(message = "카테고리 ID는 필수입니다")
                @Min(value = 1, message = "카테고리 ID는 1 이상이어야 합니다")
                Long categoryId,
        @Schema(
                        description = "배송 정책 ID",
                        example = "1",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotNull(message = "배송 정책 ID는 필수입니다")
                @Min(value = 1, message = "배송 정책 ID는 1 이상이어야 합니다")
                Long shippingPolicyId,
        @Schema(
                        description = "환불 정책 ID",
                        example = "1",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotNull(message = "환불 정책 ID는 필수입니다")
                @Min(value = 1, message = "환불 정책 ID는 1 이상이어야 합니다")
                Long refundPolicyId,
        @Schema(
                        description =
                                "옵션 타입 (COMBINATION, SINGLE, NONE). 미입력 시 optionGroups 수로 자동 결정",
                        example = "COMBINATION",
                        nullable = true)
                String optionType,
        @Schema(description = "이미지 목록", requiredMode = Schema.RequiredMode.REQUIRED)
                @NotEmpty(message = "이미지는 최소 1개 이상 필요합니다")
                @Valid
                List<ImageApiRequest> images,
        @Schema(description = "옵션 그룹 목록") @Valid List<OptionGroupApiRequest> optionGroups,
        @Schema(description = "상품 목록", requiredMode = Schema.RequiredMode.REQUIRED)
                @NotEmpty(message = "상품은 최소 1개 이상 필요합니다")
                @Valid
                List<ProductApiRequest> products,
        @Schema(description = "상세 설명", requiredMode = Schema.RequiredMode.REQUIRED)
                @NotNull(message = "상세 설명은 필수입니다")
                @Valid
                DescriptionApiRequest description,
        @Schema(description = "고시정보", requiredMode = Schema.RequiredMode.REQUIRED)
                @NotNull(message = "고시정보는 필수입니다")
                @Valid
                NoticeApiRequest notice) {

    /** 이미지 API Request. */
    @Schema(description = "이미지 데이터")
    public record ImageApiRequest(
            @Schema(
                            description = "이미지 유형 (THUMBNAIL, DETAIL)",
                            example = "THUMBNAIL",
                            requiredMode = Schema.RequiredMode.REQUIRED)
                    @NotBlank(message = "이미지 타입은 필수입니다")
                    String imageType,
            @Schema(
                            description = "원본 이미지 URL",
                            example = "https://example.com/image.jpg",
                            requiredMode = Schema.RequiredMode.REQUIRED)
                    @NotBlank(message = "원본 URL은 필수입니다")
                    String originUrl,
            @Schema(
                            description = "정렬 순서",
                            example = "0",
                            requiredMode = Schema.RequiredMode.REQUIRED)
                    @NotNull(message = "정렬 순서는 필수입니다")
                    @Min(value = 0, message = "정렬 순서는 0 이상이어야 합니다")
                    Integer sortOrder) {}

    /** 옵션 그룹 API Request. */
    @Schema(description = "옵션 그룹 데이터")
    public record OptionGroupApiRequest(
            @Schema(description = "셀러 옵션 그룹 ID (수정 시 식별용)", example = "1", nullable = true)
                    @JsonProperty("sellerOptionGroupId")
                    Long sellerOptionGroupId,
            @Schema(
                            description = "옵션 그룹명",
                            example = "색상",
                            requiredMode = Schema.RequiredMode.REQUIRED)
                    @NotBlank(message = "옵션 그룹명은 필수입니다")
                    String optionGroupName,
            @Schema(description = "정규 옵션 그룹 ID (선택)", example = "1", nullable = true)
                    @JsonProperty("canonicalOptionGroupId")
                    Long canonicalOptionGroupId,
            @Schema(
                            description = "입력 유형 (PREDEFINED: 사전 정의, FREE_INPUT: 자유 입력)",
                            example = "PREDEFINED",
                            nullable = true)
                    String inputType,
            @Schema(description = "옵션 값 목록", requiredMode = Schema.RequiredMode.REQUIRED)
                    @NotEmpty(message = "옵션 값은 최소 1개 이상 필요합니다")
                    @Valid
                    List<OptionValueApiRequest> optionValues) {}

    /** 옵션 값 API Request. */
    @Schema(description = "옵션 값 데이터")
    public record OptionValueApiRequest(
            @Schema(description = "셀러 옵션 값 ID (수정 시 식별용)", example = "1", nullable = true)
                    @JsonProperty("sellerOptionValueId")
                    Long sellerOptionValueId,
            @Schema(
                            description = "옵션 값명",
                            example = "블랙",
                            requiredMode = Schema.RequiredMode.REQUIRED)
                    @NotBlank(message = "옵션 값명은 필수입니다")
                    String optionValueName,
            @Schema(description = "정규 옵션 값 ID (선택)", example = "1", nullable = true)
                    @JsonProperty("canonicalOptionValueId")
                    Long canonicalOptionValueId,
            @Schema(
                            description = "정렬 순서",
                            example = "0",
                            requiredMode = Schema.RequiredMode.REQUIRED)
                    @NotNull(message = "정렬 순서는 필수입니다")
                    @Min(value = 0, message = "정렬 순서는 0 이상이어야 합니다")
                    Integer sortOrder) {}

    /** 이름 기반 옵션 선택 API Request. */
    @Schema(description = "선택된 옵션 데이터")
    public record SelectedOptionApiRequest(
            @Schema(
                            description = "옵션 그룹명",
                            example = "색상",
                            requiredMode = Schema.RequiredMode.REQUIRED)
                    @NotBlank(message = "옵션 그룹명은 필수입니다")
                    String optionGroupName,
            @Schema(
                            description = "옵션 값명",
                            example = "블랙",
                            requiredMode = Schema.RequiredMode.REQUIRED)
                    @NotBlank(message = "옵션 값명은 필수입니다")
                    String optionValueName) {}

    /** 상품 API Request. */
    @Schema(description = "상품 데이터")
    public record ProductApiRequest(
            @Schema(description = "상품 ID (수정 시 식별용, 신규 시 null)", example = "1", nullable = true)
                    @JsonProperty("productId")
                    Long productId,
            @Schema(
                            description = "SKU 코드",
                            example = "SKU-001",
                            requiredMode = Schema.RequiredMode.REQUIRED)
                    @NotBlank(message = "SKU 코드는 필수입니다")
                    String skuCode,
            @Schema(
                            description = "정상가",
                            example = "100000",
                            requiredMode = Schema.RequiredMode.REQUIRED)
                    @NotNull(message = "정상가는 필수입니다")
                    @Min(value = 0, message = "정상가는 0 이상이어야 합니다")
                    Integer regularPrice,
            @Schema(
                            description = "판매가",
                            example = "89000",
                            requiredMode = Schema.RequiredMode.REQUIRED)
                    @NotNull(message = "판매가는 필수입니다")
                    @Min(value = 0, message = "판매가는 0 이상이어야 합니다")
                    Integer currentPrice,
            @Schema(
                            description = "재고 수량",
                            example = "100",
                            requiredMode = Schema.RequiredMode.REQUIRED)
                    @NotNull(message = "재고 수량은 필수입니다")
                    @Min(value = 0, message = "재고 수량은 0 이상이어야 합니다")
                    Integer stockQuantity,
            @Schema(
                            description = "정렬 순서",
                            example = "0",
                            requiredMode = Schema.RequiredMode.REQUIRED)
                    @NotNull(message = "정렬 순서는 필수입니다")
                    @Min(value = 0, message = "정렬 순서는 0 이상이어야 합니다")
                    Integer sortOrder,
            @Schema(description = "선택된 옵션 목록", requiredMode = Schema.RequiredMode.REQUIRED)
                    @NotNull(message = "옵션 선택은 필수입니다")
                    @Valid
                    List<SelectedOptionApiRequest> selectedOptions) {}

    /** 상세설명 API Request. */
    @Schema(description = "상세 설명 데이터")
    public record DescriptionApiRequest(
            @Schema(
                            description = "상세 설명 내용 (HTML)",
                            example = "<p>상품 상세 설명입니다.</p>",
                            requiredMode = Schema.RequiredMode.REQUIRED)
                    @NotBlank(message = "상세설명 내용은 필수입니다")
                    String content) {}

    /** 고시정보 API Request. */
    @Schema(description = "고시정보 데이터")
    public record NoticeApiRequest(
            @Schema(
                            description = "고시 카테고리 ID",
                            example = "1",
                            requiredMode = Schema.RequiredMode.REQUIRED)
                    @NotNull(message = "고시 카테고리 ID는 필수입니다")
                    @Min(value = 1, message = "고시 카테고리 ID는 1 이상이어야 합니다")
                    Long noticeCategoryId,
            @Schema(description = "고시정보 항목 목록", requiredMode = Schema.RequiredMode.REQUIRED)
                    @NotEmpty(message = "고시정보 항목은 최소 1개 이상 필요합니다")
                    @Valid
                    List<NoticeEntryApiRequest> entries) {}

    /** 고시정보 항목 API Request. */
    @Schema(description = "고시정보 항목 데이터")
    public record NoticeEntryApiRequest(
            @Schema(
                            description = "고시 필드 ID",
                            example = "1",
                            requiredMode = Schema.RequiredMode.REQUIRED)
                    @NotNull(message = "고시 필드 ID는 필수입니다")
                    @Min(value = 1, message = "고시 필드 ID는 1 이상이어야 합니다")
                    Long noticeFieldId,
            @Schema(
                            description = "필드 값",
                            example = "면 100%",
                            requiredMode = Schema.RequiredMode.REQUIRED)
                    @NotBlank(message = "필드 값은 필수입니다")
                    String fieldValue) {}
}
