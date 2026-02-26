package com.ryuqq.marketplace.domain.canonicaloption.query;

import com.ryuqq.marketplace.domain.common.vo.SortKey;

/** 캐노니컬 옵션 그룹 정렬 키. */
public enum CanonicalOptionGroupSortKey implements SortKey {
    CREATED_AT("createdAt"),
    CODE("code");

    private final String fieldName;

    CanonicalOptionGroupSortKey(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String fieldName() {
        return fieldName;
    }

    public static CanonicalOptionGroupSortKey defaultKey() {
        return CREATED_AT;
    }
}
