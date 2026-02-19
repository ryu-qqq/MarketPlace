package com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.request;

import java.util.List;

/** 세토프 CreateProductGroup 호환 요청 DTO. */
public record LegacyCreateProductGroupRequest(
        Long productGroupId,
        String productGroupName,
        long sellerId,
        String optionType,
        String managementType,
        long categoryId,
        long brandId,
        LegacyCreateProductStatusRequest productStatus,
        LegacyCreatePriceRequest price,
        LegacyCreateProductNoticeRequest productNotice,
        LegacyCreateClothesDetailRequest clothesDetailInfo,
        LegacyCreateDeliveryNoticeRequest deliveryNotice,
        LegacyCreateRefundNoticeRequest refundNotice,
        List<LegacyCreateProductImageRequest> productImageList,
        String detailDescription,
        List<LegacyCreateOptionRequest> productOptions) {

    public LegacyCreateProductGroupRequest {
        productImageList = productImageList == null ? List.of() : List.copyOf(productImageList);
        productOptions = productOptions == null ? List.of() : List.copyOf(productOptions);
    }
}
