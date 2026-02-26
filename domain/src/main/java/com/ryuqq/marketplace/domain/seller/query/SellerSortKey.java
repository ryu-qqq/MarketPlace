package com.ryuqq.marketplace.domain.seller.query;

import com.ryuqq.marketplace.domain.common.vo.SortKey;

/**
 * Seller 정렬 키.
 *
 * <p>셀러 목록 조회 시 사용 가능한 정렬 필드를 정의합니다.
 */
public enum SellerSortKey implements SortKey {

    /** 등록일시 순 (기본값) */
    CREATED_AT("createdAt"),

    /** 셀러명 알파벳 순 */
    SELLER_NAME("sellerName"),

    /** 표시명 순 */
    DISPLAY_NAME("displayName");

    private final String fieldName;

    SellerSortKey(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String fieldName() {
        return fieldName;
    }

    /** 기본 정렬 키 (등록일시) */
    public static SellerSortKey defaultKey() {
        return CREATED_AT;
    }
}
