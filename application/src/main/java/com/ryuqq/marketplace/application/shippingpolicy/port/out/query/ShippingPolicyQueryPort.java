package com.ryuqq.marketplace.application.shippingpolicy.port.out.query;

import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.shippingpolicy.aggregate.ShippingPolicy;
import com.ryuqq.marketplace.domain.shippingpolicy.id.ShippingPolicyId;
import com.ryuqq.marketplace.domain.shippingpolicy.query.ShippingPolicySearchCriteria;
import java.util.List;
import java.util.Optional;

/** 배송 정책 Query Port. */
public interface ShippingPolicyQueryPort {

    Optional<ShippingPolicy> findById(ShippingPolicyId id);

    List<ShippingPolicy> findByIds(List<ShippingPolicyId> ids);

    /**
     * 셀러의 기본 정책 조회.
     *
     * @param sellerId 셀러 ID
     * @return 기본 배송 정책 (없으면 empty)
     */
    Optional<ShippingPolicy> findDefaultBySellerId(SellerId sellerId);

    /**
     * 셀러 ID와 정책 ID로 조회.
     *
     * @param sellerId 셀러 ID
     * @param policyId 정책 ID
     * @return 배송 정책 (없으면 empty)
     */
    Optional<ShippingPolicy> findBySellerIdAndId(SellerId sellerId, ShippingPolicyId policyId);

    List<ShippingPolicy> findByCriteria(ShippingPolicySearchCriteria criteria);

    long countByCriteria(ShippingPolicySearchCriteria criteria);

    /**
     * 셀러의 활성 정책 개수 조회.
     *
     * <p>POL-DEACT-002: 마지막 활성 정책 비활성화 검증에 사용
     *
     * @param sellerId 셀러 ID
     * @return 활성 정책 개수
     */
    long countActiveBySellerId(SellerId sellerId);
}
