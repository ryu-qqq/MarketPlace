package com.ryuqq.marketplace.application.legacyproduct.dto.setof;

/** 세토프 DB product_option 테이블 데이터. */
public record SetofProductOption(
        Long id, Long productId, Long optionGroupId, Long optionDetailId, long additionalPrice) {

    public SetofProductOption withProductId(Long productId) {
        return new SetofProductOption(
                id, productId, optionGroupId, optionDetailId, additionalPrice);
    }
}
