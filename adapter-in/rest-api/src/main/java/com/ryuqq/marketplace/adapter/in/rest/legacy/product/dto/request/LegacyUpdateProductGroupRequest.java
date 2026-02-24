package com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.request;

import jakarta.validation.Valid;
import java.util.List;

/** 세토프 UpdateProductGroup 호환 요청 DTO. */
public record LegacyUpdateProductGroupRequest(
        @Valid LegacyCreateDeliveryNoticeRequest deliveryNotice,
        @Valid LegacyCreateRefundNoticeRequest refundNotice,
        @Valid LegacyCreateProductNoticeRequest productNotice,
        @Valid List<LegacyCreateProductImageRequest> productImageList,
        @Valid LegacyUpdateProductDescriptionRequest detailDescription,
        @Valid List<LegacyCreateOptionRequest> productOptions,
        LegacyUpdateStatusRequest updateStatus) {

    public LegacyUpdateProductGroupRequest {
        productImageList = productImageList == null ? List.of() : List.copyOf(productImageList);
        productOptions = productOptions == null ? List.of() : List.copyOf(productOptions);
    }

    /** 세토프 UpdateProductGroup.UpdateStatus 호환. */
    public record LegacyUpdateStatusRequest(
            boolean productStatus,
            boolean noticeStatus,
            boolean imageStatus,
            boolean descriptionStatus,
            boolean stockOptionStatus,
            boolean deliveryStatus,
            boolean refundStatus) {}
}
