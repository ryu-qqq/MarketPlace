package com.ryuqq.marketplace.application.legacyproduct.dto.setof;

/** 세토프 DB product_group 테이블 데이터. */
public record SetofProductGroup(
        Long id,
        String productGroupName,
        long sellerId,
        long brandId,
        long categoryId,
        String optionType,
        String managementType,
        long regularPrice,
        long currentPrice,
        String soldOutYn,
        String displayYn,
        String productCondition,
        String origin,
        String styleCode) {

    public SetofProductGroup withId(Long id) {
        return new SetofProductGroup(
                id,
                productGroupName,
                sellerId,
                brandId,
                categoryId,
                optionType,
                managementType,
                regularPrice,
                currentPrice,
                soldOutYn,
                displayYn,
                productCondition,
                origin,
                styleCode);
    }
}
