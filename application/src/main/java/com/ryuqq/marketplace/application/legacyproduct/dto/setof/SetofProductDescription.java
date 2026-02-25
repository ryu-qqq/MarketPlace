package com.ryuqq.marketplace.application.legacyproduct.dto.setof;

/** 세토프 DB product_group_detail_description 테이블 데이터. */
public record SetofProductDescription(Long productGroupId, String detailDescription) {

    public SetofProductDescription withProductGroupId(Long productGroupId) {
        return new SetofProductDescription(productGroupId, detailDescription);
    }
}
