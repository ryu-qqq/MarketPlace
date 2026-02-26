package com.ryuqq.marketplace.domain.category.query;

import com.ryuqq.marketplace.domain.common.vo.SortKey;

/**
 * Category 정렬 키.
 *
 * <p>카테고리 목록 조회 시 사용 가능한 정렬 필드를 정의합니다.
 */
public enum CategorySortKey implements SortKey {

    /** 정렬 순서 (기본값) */
    SORT_ORDER("sortOrder"),

    /** 등록일시 순 */
    CREATED_AT("createdAt"),

    /** 한글명 순 */
    NAME_KO("nameKo"),

    /** 카테고리 코드 순 */
    CODE("code");

    private final String fieldName;

    CategorySortKey(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String fieldName() {
        return fieldName;
    }

    /** 기본 정렬 키 (정렬 순서) */
    public static CategorySortKey defaultKey() {
        return SORT_ORDER;
    }
}
