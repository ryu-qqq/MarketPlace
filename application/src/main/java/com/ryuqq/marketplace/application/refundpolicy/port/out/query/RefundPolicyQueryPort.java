package com.ryuqq.marketplace.application.refundpolicy.port.out.query;

import com.ryuqq.marketplace.domain.refundpolicy.aggregate.RefundPolicy;
import com.ryuqq.marketplace.domain.refundpolicy.id.RefundPolicyId;
import com.ryuqq.marketplace.domain.refundpolicy.query.RefundPolicySearchCriteria;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import java.util.List;
import java.util.Optional;

/** 환불 정책 Query Port. */
public interface RefundPolicyQueryPort {

    Optional<RefundPolicy> findById(RefundPolicyId id);

    List<RefundPolicy> findByIds(List<RefundPolicyId> ids);

    /**
     * 셀러의 기본 정책 조회.
     *
     * @param sellerId 셀러 ID
     * @return 기본 환불 정책 (없으면 empty)
     */
    Optional<RefundPolicy> findDefaultBySellerId(SellerId sellerId);

    /**
     * 셀러 ID와 정책 ID로 조회.
     *
     * @param sellerId 셀러 ID
     * @param policyId 정책 ID
     * @return 환불 정책 (없으면 empty)
     */
    Optional<RefundPolicy> findBySellerIdAndId(SellerId sellerId, RefundPolicyId policyId);

    List<RefundPolicy> findByCriteria(RefundPolicySearchCriteria criteria);

    long countByCriteria(RefundPolicySearchCriteria criteria);

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
