package com.ryuqq.marketplace.application.legacyproduct.dto.setof;

/** 세토프 DB product_delivery 테이블 데이터. */
public record SetofProductDelivery(
        Long productGroupId,
        String deliveryArea,
        long deliveryFee,
        int deliveryPeriodAverage,
        String returnMethodDomestic,
        String returnCourierDomestic,
        int returnChargeDomestic,
        String returnExchangeAreaDomestic) {

    public SetofProductDelivery withProductGroupId(Long productGroupId) {
        return new SetofProductDelivery(
                productGroupId,
                deliveryArea,
                deliveryFee,
                deliveryPeriodAverage,
                returnMethodDomestic,
                returnCourierDomestic,
                returnChargeDomestic,
                returnExchangeAreaDomestic);
    }
}
