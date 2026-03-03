package com.ryuqq.marketplace.domain.outboundproduct.query;

import com.ryuqq.marketplace.domain.common.vo.SortKey;

/**
 * OMS 상품 목록 정렬 키.
 *
 * @author development-team
 * @since 1.0.0
 */
public enum OmsProductSortKey implements SortKey {
    CREATED_AT("createdAt"),
    UPDATED_AT("updatedAt"),
    PRODUCT_GROUP_NAME("productGroupName");

    private final String fieldName;

    OmsProductSortKey(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String fieldName() {
        return fieldName;
    }

    public static OmsProductSortKey defaultKey() {
        return CREATED_AT;
    }
}
