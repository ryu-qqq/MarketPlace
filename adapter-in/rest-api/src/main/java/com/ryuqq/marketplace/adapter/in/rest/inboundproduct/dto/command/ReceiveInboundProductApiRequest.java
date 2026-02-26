package com.ryuqq.marketplace.adapter.in.rest.inboundproduct.dto.command;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

/** 인바운드 상품 수신 요청 DTO (크롤링 등 외부 소스용). */
public record ReceiveInboundProductApiRequest(
        @Min(value = 1, message = "인바운드 소스 ID는 1 이상이어야 합니다") long inboundSourceId,
        @NotBlank(message = "외부 상품 코드는 필수입니다") String externalProductCode,
        @NotBlank(message = "상품명은 필수입니다") @Size(max = 500, message = "상품명은 500자 이하여야 합니다")
                String productName,
        @NotBlank(message = "외부 브랜드 코드는 필수입니다") String externalBrandCode,
        @NotBlank(message = "외부 카테고리 코드는 필수입니다") String externalCategoryCode,
        @Min(value = 1, message = "셀러 ID는 1 이상이어야 합니다") long sellerId,
        @Min(value = 0, message = "정가는 0 이상이어야 합니다") int regularPrice,
        @Min(value = 0, message = "현재가는 0 이상이어야 합니다") int currentPrice,
        @NotBlank(message = "옵션 타입은 필수입니다") String optionType,
        String descriptionHtml,
        @Valid @NotNull(message = "이미지 목록은 필수입니다") @Size(min = 1, message = "이미지는 최소 1개 이상이어야 합니다")
                List<ImageRequest> images,
        @Valid List<OptionGroupRequest> optionGroups,
        @Valid @NotNull(message = "상품 목록은 필수입니다") @Size(min = 1, message = "상품은 최소 1개 이상이어야 합니다")
                List<ProductRequest> products,
        @Valid List<NoticeEntryRequest> noticeEntries) {

    public ReceiveInboundProductApiRequest {
        images = images != null ? List.copyOf(images) : List.of();
        optionGroups = optionGroups != null ? List.copyOf(optionGroups) : List.of();
        products = products != null ? List.copyOf(products) : List.of();
        noticeEntries = noticeEntries != null ? List.copyOf(noticeEntries) : List.of();
    }

    public record ImageRequest(
            @NotBlank(message = "이미지 타입은 필수입니다") String imageType,
            @NotBlank(message = "이미지 URL은 필수입니다") String originUrl,
            @Min(value = 0, message = "정렬 순서는 0 이상이어야 합니다") int sortOrder) {}

    public record OptionGroupRequest(
            @NotBlank(message = "옵션 그룹명은 필수입니다") String optionGroupName,
            String inputType,
            @Valid List<OptionValueRequest> optionValues) {

        public OptionGroupRequest {
            optionValues = optionValues != null ? List.copyOf(optionValues) : List.of();
        }

        public record OptionValueRequest(
                @NotBlank(message = "옵션 값명은 필수입니다") String optionValueName,
                @Min(value = 0, message = "정렬 순서는 0 이상이어야 합니다") int sortOrder) {}
    }

    public record ProductRequest(
            String skuCode,
            @Min(value = 0, message = "정가는 0 이상이어야 합니다") int regularPrice,
            @Min(value = 0, message = "현재가는 0 이상이어야 합니다") int currentPrice,
            @Min(value = 0, message = "재고 수량은 0 이상이어야 합니다") int stockQuantity,
            @Min(value = 0, message = "정렬 순서는 0 이상이어야 합니다") int sortOrder,
            @Valid List<SelectedOptionRequest> selectedOptions) {

        public ProductRequest {
            selectedOptions = selectedOptions != null ? List.copyOf(selectedOptions) : List.of();
        }

        public record SelectedOptionRequest(
                @NotBlank(message = "옵션 그룹명은 필수입니다") String optionGroupName,
                @NotBlank(message = "옵션 값명은 필수입니다") String optionValueName) {}
    }

    public record NoticeEntryRequest(
            @NotBlank(message = "고시정보 필드 코드는 필수입니다") String fieldCode,
            @NotBlank(message = "고시정보 필드 값은 필수입니다") String fieldValue) {}
}
