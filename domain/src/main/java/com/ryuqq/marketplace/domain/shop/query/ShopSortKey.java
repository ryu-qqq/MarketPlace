package com.ryuqq.marketplace.domain.shop.query;

import com.ryuqq.marketplace.domain.common.vo.SortKey;

/**
 * Shop 정렬 키.
 *
 * <p>외부몰 목록 조회 시 사용 가능한 정렬 필드를 정의합니다.
 */
public enum ShopSortKey implements SortKey {

    /** 등록일시 순 (기본값) */
    CREATED_AT("createdAt"),

    /** 수정일시 순 */
    UPDATED_AT("updatedAt"),

    /** 외부몰명 순 */
    SHOP_NAME("shopName");

    private final String fieldName;

    ShopSortKey(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String fieldName() {
        return fieldName;
    }

    /** 기본 정렬 키 (등록일시) */
    public static ShopSortKey defaultKey() {
        return CREATED_AT;
    }
}
