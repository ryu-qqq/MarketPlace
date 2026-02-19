package com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.command;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * 상품 그룹 등록 API 요청.
 *
 * <p>Validation을 포함한 REST API Layer DTO입니다.
 */
public record RegisterProductGroupApiRequest(
        @NotNull(message = "셀러 ID는 필수입니다") @Min(value = 1, message = "셀러 ID는 1 이상이어야 합니다")
                Long sellerId,
        @NotNull(message = "브랜드 ID는 필수입니다") @Min(value = 1, message = "브랜드 ID는 1 이상이어야 합니다")
                Long brandId,
        @NotNull(message = "카테고리 ID는 필수입니다") @Min(value = 1, message = "카테고리 ID는 1 이상이어야 합니다")
                Long categoryId,
        @NotNull(message = "배송 정책 ID는 필수입니다") @Min(value = 1, message = "배송 정책 ID는 1 이상이어야 합니다")
                Long shippingPolicyId,
        @NotNull(message = "환불 정책 ID는 필수입니다") @Min(value = 1, message = "환불 정책 ID는 1 이상이어야 합니다")
                Long refundPolicyId,
        @NotBlank(message = "상품 그룹명은 필수입니다") @Size(max = 200, message = "상품 그룹명은 200자 이하여야 합니다")
                String productGroupName,
        @NotBlank(message = "옵션 타입은 필수입니다") String optionType,
        @Valid List<ImageApiRequest> images,
        @Valid List<OptionGroupApiRequest> optionGroups,
        @Valid List<ProductApiRequest> products,
        @Valid DescriptionApiRequest description,
        @Valid NoticeApiRequest notice) {

    public record ImageApiRequest(
            @NotBlank(message = "이미지 타입은 필수입니다") String imageType,
            @NotBlank(message = "원본 URL은 필수입니다") String originUrl,
            @Min(value = 0, message = "정렬 순서는 0 이상이어야 합니다") int sortOrder) {}

    public record OptionGroupApiRequest(
            @NotBlank(message = "옵션 그룹명은 필수입니다") String optionGroupName,
            Long canonicalOptionGroupId,
            @Valid
                    @NotNull(message = "옵션 값 목록은 필수입니다")
                    @Size(min = 1, message = "옵션 값은 최소 1개 이상이어야 합니다")
                    List<OptionValueApiRequest> optionValues) {}

    public record OptionValueApiRequest(
            @NotBlank(message = "옵션 값명은 필수입니다") String optionValueName,
            Long canonicalOptionValueId,
            @Min(value = 0, message = "정렬 순서는 0 이상이어야 합니다") int sortOrder) {}

    public record SelectedOptionApiRequest(
            @NotBlank(message = "옵션 그룹명은 필수입니다") String optionGroupName,
            @NotBlank(message = "옵션 값명은 필수입니다") String optionValueName) {}

    public record ProductApiRequest(
            @NotBlank(message = "SKU 코드는 필수입니다") String skuCode,
            @Min(value = 0, message = "정가는 0 이상이어야 합니다") int regularPrice,
            @Min(value = 0, message = "현재가는 0 이상이어야 합니다") int currentPrice,
            @Min(value = 0, message = "재고 수량은 0 이상이어야 합니다") int stockQuantity,
            @Min(value = 0, message = "정렬 순서는 0 이상이어야 합니다") int sortOrder,
            @NotNull(message = "옵션 선택 목록은 필수입니다") @Valid
                    List<SelectedOptionApiRequest> selectedOptions) {}

    public record DescriptionApiRequest(String content) {}

    public record NoticeApiRequest(
            @NotNull(message = "고시 카테고리 ID는 필수입니다")
                    @Min(value = 1, message = "고시 카테고리 ID는 1 이상이어야 합니다")
                    Long noticeCategoryId,
            @Valid
                    @NotNull(message = "고시 항목 목록은 필수입니다")
                    @Size(min = 1, message = "고시 항목은 최소 1개 이상이어야 합니다")
                    List<NoticeEntryApiRequest> entries) {}

    public record NoticeEntryApiRequest(
            @NotNull(message = "고시 필드 ID는 필수입니다") @Min(value = 1, message = "고시 필드 ID는 1 이상이어야 합니다")
                    Long noticeFieldId,
            @NotBlank(message = "고시 필드 값은 필수입니다") String fieldValue) {}
}
