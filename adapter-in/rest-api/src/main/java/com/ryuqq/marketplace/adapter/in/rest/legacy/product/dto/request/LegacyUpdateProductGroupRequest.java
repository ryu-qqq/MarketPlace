package com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.request;

import java.util.List;

/** 세토프 UpdateProductGroup 호환 요청 DTO. */
public record LegacyUpdateProductGroupRequest(
        LegacyCreateDeliveryNoticeRequest deliveryNotice,
        LegacyCreateRefundNoticeRequest refundNotice,
        LegacyCreateProductNoticeRequest productNotice,
        List<LegacyCreateProductImageRequest> productImageList,
        LegacyUpdateProductDescriptionRequest detailDescription,
        List<LegacyCreateOptionRequest> productOptions,
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
