package com.ryuqq.marketplace.domain.selleraddress.query;

import com.ryuqq.marketplace.domain.common.vo.SortKey;

/** 셀러 주소 정렬 키. */
public enum SellerAddressSortKey implements SortKey {

    /** 등록일시 순 (기본값) */
    CREATED_AT("createdAt"),

    /** 주소 유형 순 */
    ADDRESS_TYPE("addressType");

    private final String fieldName;

    SellerAddressSortKey(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String fieldName() {
        return fieldName;
    }

    /** 기본 정렬 키 (등록일시) */
    public static SellerAddressSortKey defaultKey() {
        return CREATED_AT;
    }
}
