package com.ryuqq.marketplace.domain.inboundsource.query;

import com.ryuqq.marketplace.domain.common.vo.SortKey;

/** InboundSource 정렬 키. */
public enum InboundSourceSortKey implements SortKey {

    /** 등록일시 순 (기본값) */
    CREATED_AT("createdAt");

    private final String fieldName;

    InboundSourceSortKey(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String fieldName() {
        return fieldName;
    }

    /** 기본 정렬 키 (등록일시) */
    public static InboundSourceSortKey defaultKey() {
        return CREATED_AT;
    }
}
