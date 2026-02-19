package com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.command;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public record UpdateProductGroupFullApiRequest(
        @NotBlank(message = "상품 그룹명은 필수입니다") @Size(max = 200, message = "상품 그룹명은 200자 이내여야 합니다")
                String productGroupName,
        @NotNull(message = "브랜드 ID는 필수입니다") @Min(value = 1, message = "브랜드 ID는 1 이상이어야 합니다")
                Long brandId,
        @NotNull(message = "카테고리 ID는 필수입니다") @Min(value = 1, message = "카테고리 ID는 1 이상이어야 합니다")
                Long categoryId,
        @NotNull(message = "배송 정책 ID는 필수입니다") @Min(value = 1, message = "배송 정책 ID는 1 이상이어야 합니다")
                Long shippingPolicyId,
        @NotNull(message = "환불 정책 ID는 필수입니다") @Min(value = 1, message = "환불 정책 ID는 1 이상이어야 합니다")
                Long refundPolicyId,
        @NotEmpty(message = "이미지는 최소 1개 이상 필요합니다") @Valid List<ImageApiRequest> images,
        @Valid List<OptionGroupApiRequest> optionGroups,
        @NotEmpty(message = "상품은 최소 1개 이상 필요합니다") @Valid List<ProductApiRequest> products,
        @Valid DescriptionApiRequest description,
        @Valid NoticeApiRequest notice) {

    /** 이미지 API Request. */
    public record ImageApiRequest(
            @NotBlank(message = "이미지 타입은 필수입니다") String imageType,
            @NotBlank(message = "원본 URL은 필수입니다") String originUrl,
            @NotNull(message = "정렬 순서는 필수입니다") @Min(value = 0, message = "정렬 순서는 0 이상이어야 합니다")
                    Integer sortOrder) {}

    /** 옵션 그룹 API Request. */
    public record OptionGroupApiRequest(
            @JsonProperty("sellerOptionGroupId") Long sellerOptionGroupId,
            @NotBlank(message = "옵션 그룹명은 필수입니다") String optionGroupName,
            @JsonProperty("canonicalOptionGroupId") Long canonicalOptionGroupId,
            @NotEmpty(message = "옵션 값은 최소 1개 이상 필요합니다") @Valid
                    List<OptionValueApiRequest> optionValues) {}

    /** 옵션 값 API Request. */
    public record OptionValueApiRequest(
            @JsonProperty("sellerOptionValueId") Long sellerOptionValueId,
            @NotBlank(message = "옵션 값명은 필수입니다") String optionValueName,
            @JsonProperty("canonicalOptionValueId") Long canonicalOptionValueId,
            @NotNull(message = "정렬 순서는 필수입니다") @Min(value = 0, message = "정렬 순서는 0 이상이어야 합니다")
                    Integer sortOrder) {}

    /** 이름 기반 옵션 선택 API Request. */
    public record SelectedOptionApiRequest(
            @NotBlank(message = "옵션 그룹명은 필수입니다") String optionGroupName,
            @NotBlank(message = "옵션 값명은 필수입니다") String optionValueName) {}

    /** 상품 API Request. */
    public record ProductApiRequest(
            @JsonProperty("productId") Long productId,
            @NotBlank(message = "SKU 코드는 필수입니다") String skuCode,
            @NotNull(message = "정상가는 필수입니다") @Min(value = 0, message = "정상가는 0 이상이어야 합니다")
                    Integer regularPrice,
            @NotNull(message = "판매가는 필수입니다") @Min(value = 0, message = "판매가는 0 이상이어야 합니다")
                    Integer currentPrice,
            @NotNull(message = "재고 수량은 필수입니다") @Min(value = 0, message = "재고 수량은 0 이상이어야 합니다")
                    Integer stockQuantity,
            @NotNull(message = "정렬 순서는 필수입니다") @Min(value = 0, message = "정렬 순서는 0 이상이어야 합니다")
                    Integer sortOrder,
            @NotNull(message = "옵션 선택은 필수입니다") @Valid
                    List<SelectedOptionApiRequest> selectedOptions) {}

    /** 상세설명 API Request. */
    public record DescriptionApiRequest(@NotBlank(message = "상세설명 내용은 필수입니다") String content) {}

    /** 고시정보 API Request. */
    public record NoticeApiRequest(
            @NotNull(message = "고시 카테고리 ID는 필수입니다")
                    @Min(value = 1, message = "고시 카테고리 ID는 1 이상이어야 합니다")
                    Long noticeCategoryId,
            @NotEmpty(message = "고시정보 항목은 최소 1개 이상 필요합니다") @Valid
                    List<NoticeEntryApiRequest> entries) {}

    /** 고시정보 항목 API Request. */
    public record NoticeEntryApiRequest(
            @NotNull(message = "고시 필드 ID는 필수입니다") @Min(value = 1, message = "고시 필드 ID는 1 이상이어야 합니다")
                    Long noticeFieldId,
            @NotBlank(message = "필드 값은 필수입니다") String fieldValue) {}
}
