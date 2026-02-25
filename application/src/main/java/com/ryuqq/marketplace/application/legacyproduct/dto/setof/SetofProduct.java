package com.ryuqq.marketplace.application.legacyproduct.dto.setof;

/** 세토프 DB product 테이블 데이터. */
public record SetofProduct(Long id, Long productGroupId, String soldOutYn, String displayYn) {

    public SetofProduct withProductGroupId(Long productGroupId) {
        return new SetofProduct(id, productGroupId, soldOutYn, displayYn);
    }

    public SetofProduct withId(Long id) {
        return new SetofProduct(id, productGroupId, soldOutYn, displayYn);
    }
}
