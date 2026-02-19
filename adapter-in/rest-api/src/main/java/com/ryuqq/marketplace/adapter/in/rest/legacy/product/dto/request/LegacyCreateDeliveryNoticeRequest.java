package com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.request;

/** 세토프 CreateDeliveryNotice 호환 요청 DTO. */
public record LegacyCreateDeliveryNoticeRequest(
        String deliveryArea, long deliveryFee, int deliveryPeriodAverage) {}
