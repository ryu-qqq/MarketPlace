package com.ryuqq.marketplace.domain.shippingpolicy.query;

import com.ryuqq.marketplace.domain.common.vo.SortKey;

/**
 * ShippingPolicy 정렬 키.
 *
 * <p>배송 정책 목록 조회 시 사용 가능한 정렬 필드를 정의합니다.
 */
public enum ShippingPolicySortKey implements SortKey {

    /** 등록일시 순 (기본값) */
    CREATED_AT("createdAt"),

    /** 정책명 순 */
    POLICY_NAME("policyName"),

    /** 기본 배송비 순 */
    BASE_FEE("baseFee");

    private final String fieldName;

    ShippingPolicySortKey(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String fieldName() {
        return fieldName;
    }

    /** 기본 정렬 키 (등록일시) */
    public static ShippingPolicySortKey defaultKey() {
        return CREATED_AT;
    }
}
