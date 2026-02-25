package com.ryuqq.marketplace.application.legacyproduct.dto.setof;

/** 세토프 DB product_notice 테이블 데이터. */
public record SetofProductNotice(
        Long productGroupId,
        String material,
        String color,
        String size,
        String maker,
        String origin,
        String washingMethod,
        String yearMonthDay,
        String assuranceStandard,
        String asPhone) {

    public SetofProductNotice withProductGroupId(Long productGroupId) {
        return new SetofProductNotice(
                productGroupId,
                material,
                color,
                size,
                maker,
                origin,
                washingMethod,
                yearMonthDay,
                assuranceStandard,
                asPhone);
    }
}
