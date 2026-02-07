package com.ryuqq.marketplace.domain.sellerapplication.query;

import com.ryuqq.marketplace.domain.common.vo.SortKey;

/**
 * SellerApplication 정렬 키.
 *
 * <p>입점 신청 목록 조회 시 사용 가능한 정렬 필드를 정의합니다.
 */
public enum SellerApplicationSortKey implements SortKey {

    /** 입점 신청일시 순 (기본값) */
    APPLIED_AT("appliedAt");

    private final String fieldName;

    SellerApplicationSortKey(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String fieldName() {
        return fieldName;
    }

    /** 기본 정렬 키 (입점 신청일시) */
    public static SellerApplicationSortKey defaultKey() {
        return APPLIED_AT;
    }
}
