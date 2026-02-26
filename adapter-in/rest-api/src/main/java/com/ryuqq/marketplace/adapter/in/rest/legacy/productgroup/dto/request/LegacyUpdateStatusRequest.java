package com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.dto.request;

/** 세토프 UpdateProductGroup.UpdateStatus 호환 요청 DTO. */
public record LegacyUpdateStatusRequest(
        boolean productStatus,
        boolean noticeStatus,
        boolean imageStatus,
        boolean descriptionStatus,
        boolean stockOptionStatus,
        boolean deliveryStatus,
        boolean refundStatus) {}
