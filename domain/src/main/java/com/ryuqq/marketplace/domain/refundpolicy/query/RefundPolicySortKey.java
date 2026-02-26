package com.ryuqq.marketplace.domain.refundpolicy.query;

import com.ryuqq.marketplace.domain.common.vo.SortKey;

/**
 * RefundPolicy 정렬 키.
 *
 * <p>환불 정책 목록 조회 시 사용 가능한 정렬 필드를 정의합니다.
 */
public enum RefundPolicySortKey implements SortKey {

    /** 등록일시 순 (기본값) */
    CREATED_AT("createdAt"),

    /** 정책명 순 */
    POLICY_NAME("policyName"),

    /** 반품 가능 기간 순 */
    RETURN_PERIOD_DAYS("returnPeriodDays");

    private final String fieldName;

    RefundPolicySortKey(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String fieldName() {
        return fieldName;
    }

    /** 기본 정렬 키 (등록일시) */
    public static RefundPolicySortKey defaultKey() {
        return CREATED_AT;
    }
}
