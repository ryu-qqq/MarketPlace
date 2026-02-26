package com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.dto.request;

import com.ryuqq.marketplace.adapter.in.rest.legacy.notice.dto.request.LegacyCreateProductNoticeRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.request.LegacyCreateOptionRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroupdetaildescription.dto.request.LegacyUpdateProductDescriptionRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroupimage.dto.request.LegacyCreateProductImageRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/** 세토프 UpdateProductGroup 호환 요청 DTO. */
public record LegacyUpdateProductGroupRequest(
        @Valid LegacyProductGroupDetailsRequest productGroupDetails,
        @Valid LegacyCreateDeliveryNoticeRequest deliveryNotice,
        @Valid LegacyCreateRefundNoticeRequest refundNotice,
        @Valid LegacyCreateProductNoticeRequest productNotice,
        @Valid List<LegacyCreateProductImageRequest> productImageList,
        @Valid LegacyUpdateProductDescriptionRequest detailDescription,
        @Valid List<LegacyCreateOptionRequest> productOptions,
        @Valid @NotNull(message = "수정 상태 정보는 필수입니다.") LegacyUpdateStatusRequest updateStatus) {

    public LegacyUpdateProductGroupRequest {
        productImageList = productImageList == null ? List.of() : List.copyOf(productImageList);
        productOptions = productOptions == null ? List.of() : List.copyOf(productOptions);
    }
}
