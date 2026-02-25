package com.ryuqq.marketplace.application.legacyproduct.dto.setof;

/** 세토프 DB product_group_image 테이블 데이터. */
public record SetofProductImage(
        Long id, Long productGroupId, String imageType, String imageUrl, String originUrl) {

    public SetofProductImage withProductGroupId(Long productGroupId) {
        return new SetofProductImage(id, productGroupId, imageType, imageUrl, originUrl);
    }
}
