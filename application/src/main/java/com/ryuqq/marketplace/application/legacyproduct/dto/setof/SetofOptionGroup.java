package com.ryuqq.marketplace.application.legacyproduct.dto.setof;

/** 세토프 DB option_group 테이블 데이터. */
public record SetofOptionGroup(Long id, String optionName) {

    public SetofOptionGroup withId(Long id) {
        return new SetofOptionGroup(id, optionName);
    }
}
