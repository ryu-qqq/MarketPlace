package com.ryuqq.marketplace.application.legacyproduct.dto.setof;

/** 세토프 DB option_detail 테이블 데이터. */
public record SetofOptionDetail(Long id, Long optionGroupId, String optionValue) {

    public SetofOptionDetail withId(Long id) {
        return new SetofOptionDetail(id, optionGroupId, optionValue);
    }

    public SetofOptionDetail withOptionGroupId(Long optionGroupId) {
        return new SetofOptionDetail(id, optionGroupId, optionValue);
    }
}
