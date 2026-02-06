package com.ryuqq.marketplace.domain.brand.query;

import com.ryuqq.marketplace.domain.common.vo.SortKey;

/**
 * Brand 정렬 키.
 *
 * <p>브랜드 목록 조회 시 사용 가능한 정렬 필드를 정의합니다.
 */
public enum BrandSortKey implements SortKey {

    /** 등록일시 순 (기본값) */
    CREATED_AT("createdAt"),

    /** 한글명 순 */
    NAME_KO("nameKo"),

    /** 수정일시 순 */
    UPDATED_AT("updatedAt");

    private final String fieldName;

    BrandSortKey(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String fieldName() {
        return fieldName;
    }

    /** 기본 정렬 키 (등록일시) */
    public static BrandSortKey defaultKey() {
        return CREATED_AT;
    }
}
