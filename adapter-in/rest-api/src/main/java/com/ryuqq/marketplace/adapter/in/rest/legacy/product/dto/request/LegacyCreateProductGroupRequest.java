package com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.List;
import org.hibernate.validator.constraints.Length;

/** 세토프 CreateProductGroup 호환 요청 DTO. */
public record LegacyCreateProductGroupRequest(
        @Length(max = 200, message = "상품 그룹명은 200자를 초과할 수 없습니다.") String productGroupName,
        @Positive(message = "셀러 ID는 0보다 커야 합니다.") long sellerId,
        @NotBlank(message = "옵션 타입은 필수입니다.") String optionType,
        @NotBlank(message = "관리 타입은 필수입니다.") String managementType,
        @Positive(message = "카테고리 ID는 0보다 커야 합니다.") long categoryId,
        @Positive(message = "브랜드 ID는 0보다 커야 합니다.") long brandId,
        @Valid LegacyCreateProductStatusRequest productStatus,
        @Valid @NotNull(message = "가격 정보는 필수입니다.") LegacyCreatePriceRequest price,
        @Valid LegacyCreateProductNoticeRequest productNotice,
        @Valid LegacyCreateClothesDetailRequest clothesDetailInfo,
        @Valid LegacyCreateDeliveryNoticeRequest deliveryNotice,
        @Valid LegacyCreateRefundNoticeRequest refundNotice,
        @Valid @Size(min = 1, max = 10, message = "상품 이미지는 1개 이상 10개 이하로 입력해야 합니다.")
                List<LegacyCreateProductImageRequest> productImageList,
        @NotNull(message = "상세 설명은 필수입니다.") String detailDescription,
        @Valid @Size(min = 1, message = "상품 옵션은 최소 1개 이상이어야 합니다.")
                List<LegacyCreateOptionRequest> productOptions) {

    public LegacyCreateProductGroupRequest {
        productImageList = productImageList == null ? List.of() : List.copyOf(productImageList);
        productOptions = productOptions == null ? List.of() : List.copyOf(productOptions);
    }
}
