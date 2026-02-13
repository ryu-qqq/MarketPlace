package com.ryuqq.marketplace.domain.shipment.query;

import com.ryuqq.marketplace.domain.common.vo.SortKey;

/** 배송 정렬 키. */
public enum ShipmentSortKey implements SortKey {

    /** 등록일시 순 (기본값) */
    CREATED_AT("createdAt"),

    /** 발송일시 순 */
    SHIPPED_AT("shippedAt"),

    /** 배송완료일시 순 */
    DELIVERED_AT("deliveredAt");

    private final String fieldName;

    ShipmentSortKey(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String fieldName() {
        return fieldName;
    }

    /** 기본 정렬 키 (등록일시) */
    public static ShipmentSortKey defaultKey() {
        return CREATED_AT;
    }
}
