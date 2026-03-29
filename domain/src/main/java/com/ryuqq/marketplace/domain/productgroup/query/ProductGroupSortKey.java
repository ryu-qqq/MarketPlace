package com.ryuqq.marketplace.domain.productgroup.query;

import com.ryuqq.marketplace.domain.common.vo.SortKey;

/**
 * ProductGroup 정렬 키.
 *
 * <p>상품 그룹 목록 조회 시 사용 가능한 정렬 필드를 정의합니다.
 */
public enum ProductGroupSortKey implements SortKey {

    /** 상품그룹 ID 순 */
    ID("id"),

    /** 등록일시 순 (기본값) */
    CREATED_AT("createdAt"),

    /** 수정일시 순 */
    UPDATED_AT("updatedAt"),

    /** 상품명 순 */
    NAME("productGroupName");

    private final String fieldName;

    ProductGroupSortKey(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String fieldName() {
        return fieldName;
    }

    /** 기본 정렬 키 (등록일시) */
    public static ProductGroupSortKey defaultKey() {
        return CREATED_AT;
    }
}
