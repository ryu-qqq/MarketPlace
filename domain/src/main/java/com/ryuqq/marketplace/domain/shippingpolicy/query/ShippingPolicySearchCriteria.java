package com.ryuqq.marketplace.domain.shippingpolicy.query;

import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.seller.id.SellerId;

/**
 * ShippingPolicy 검색 조건 Criteria.
 *
 * <p>배송 정책 목록 조회 시 사용하는 검색 조건과 페이징 정보를 정의합니다.
 *
 * @param sellerId 셀러 ID (필수)
 * @param queryContext 정렬 및 페이징 정보
 * @param active 활성화 여부 (true: 활성만, false: 비활성만, null: 전체)
 */
public record ShippingPolicySearchCriteria(
        SellerId sellerId,
        QueryContext<ShippingPolicySortKey> queryContext,
        Boolean active) {

    /**
     * 검색 조건 생성
     *
     * @param sellerId 셀러 ID
     * @param queryContext 정렬 및 페이징 정보
     * @param active 활성화 여부
     * @return ShippingPolicySearchCriteria
     */
    public static ShippingPolicySearchCriteria of(
            SellerId sellerId,
            QueryContext<ShippingPolicySortKey> queryContext,
            Boolean active) {
        return new ShippingPolicySearchCriteria(sellerId, queryContext, active);
    }

    /**
     * 기본 검색 조건 생성 (등록순 내림차순)
     *
     * @param sellerId 셀러 ID
     * @return ShippingPolicySearchCriteria
     */
    public static ShippingPolicySearchCriteria defaultCriteria(SellerId sellerId) {
        return new ShippingPolicySearchCriteria(
                sellerId, QueryContext.defaultOf(ShippingPolicySortKey.defaultKey()), null);
    }

    /** 페이지 크기 반환 (편의 메서드) */
    public int size() {
        return queryContext.size();
    }

    /** 오프셋 반환 (편의 메서드) */
    public long offset() {
        return queryContext.offset();
    }

    /** 현재 페이지 번호 반환 (편의 메서드) */
    public int page() {
        return queryContext.page();
    }

    /** 셀러 ID 값 반환 (편의 메서드) */
    public Long sellerIdValue() {
        return sellerId.value();
    }
}
