package com.ryuqq.marketplace.domain.selleradmin.query;

import com.ryuqq.marketplace.domain.common.vo.SortKey;

/**
 * SellerAdmin 정렬 키.
 *
 * <p>셀러 관리자 목록 조회 시 사용 가능한 정렬 필드를 정의합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public enum SellerAdminSortKey implements SortKey {

    /** 생성일시 순 (기본값) */
    CREATED_AT("createdAt");

    private final String fieldName;

    SellerAdminSortKey(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String fieldName() {
        return fieldName;
    }

    /** 기본 정렬 키 (생성일시) */
    public static SellerAdminSortKey defaultKey() {
        return CREATED_AT;
    }
}
