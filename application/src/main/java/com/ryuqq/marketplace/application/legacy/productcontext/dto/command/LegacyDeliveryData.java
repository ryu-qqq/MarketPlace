package com.ryuqq.marketplace.application.legacy.productcontext.dto.command;

/**
 * 레거시 배송 데이터.
 *
 * <p>디폴트 배송정책과 비교하기 위한 레거시 배송 정보.
 */
public record LegacyDeliveryData(
        String deliveryArea,
        long deliveryFee,
        int deliveryPeriodAverage) {}
