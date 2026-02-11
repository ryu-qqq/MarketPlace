package com.ryuqq.marketplace.domain.notice.query;

import com.ryuqq.marketplace.domain.common.vo.SortKey;

/** 고시정보 카테고리 정렬 키. */
public enum NoticeCategorySortKey implements SortKey {
    CREATED_AT("createdAt"),
    CODE("code");

    private final String fieldName;

    NoticeCategorySortKey(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String fieldName() {
        return fieldName;
    }

    public static NoticeCategorySortKey defaultKey() {
        return CREATED_AT;
    }
}
