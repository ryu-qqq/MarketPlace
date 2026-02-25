package com.ryuqq.marketplace.domain.legacy.productgroup.vo;

/** 레거시(세토프) 배송/반품 정보 Value Object. */
public record LegacyProductDelivery(
        String deliveryArea,
        long deliveryFee,
        int deliveryPeriodAverage,
        ReturnMethod returnMethodDomestic,
        ShipmentCompanyCode returnCourierDomestic,
        int returnChargeDomestic,
        String returnExchangeAreaDomestic) {}
