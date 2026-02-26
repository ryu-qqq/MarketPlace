package com.ryuqq.marketplace.application.refundpolicy.manager;

import com.ryuqq.marketplace.application.refundpolicy.port.out.query.RefundPolicyQueryPort;
import com.ryuqq.marketplace.domain.refundpolicy.aggregate.RefundPolicy;
import com.ryuqq.marketplace.domain.refundpolicy.exception.RefundPolicyException;
import com.ryuqq.marketplace.domain.refundpolicy.exception.RefundPolicyNotFoundForSellerException;
import com.ryuqq.marketplace.domain.refundpolicy.id.RefundPolicyId;
import com.ryuqq.marketplace.domain.refundpolicy.query.RefundPolicySearchCriteria;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** 환불 정책 Read Manager. */
@Component
public class RefundPolicyReadManager {

    private final RefundPolicyQueryPort queryPort;

    public RefundPolicyReadManager(RefundPolicyQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    @Transactional(readOnly = true)
    public RefundPolicy getById(RefundPolicyId id) {
        return queryPort.findById(id).orElseThrow(RefundPolicyException::policyNotFound);
    }

    @Transactional(readOnly = true)
    public List<RefundPolicy> getByIds(List<RefundPolicyId> ids) {
        return queryPort.findByIds(ids);
    }

    @Transactional(readOnly = true)
    public Optional<RefundPolicy> findDefaultBySellerId(SellerId sellerId) {
        return queryPort.findDefaultBySellerId(sellerId);
    }

    @Transactional(readOnly = true)
    public RefundPolicy getBySellerIdAndId(SellerId sellerId, RefundPolicyId policyId) {
        return queryPort
                .findBySellerIdAndId(sellerId, policyId)
                .orElseThrow(
                        () ->
                                new RefundPolicyNotFoundForSellerException(
                                        sellerId.value(), policyId.value()));
    }

    @Transactional(readOnly = true)
    public Optional<RefundPolicy> findBySellerIdAndId(SellerId sellerId, RefundPolicyId policyId) {
        return queryPort.findBySellerIdAndId(sellerId, policyId);
    }

    @Transactional(readOnly = true)
    public List<RefundPolicy> findByCriteria(RefundPolicySearchCriteria criteria) {
        return queryPort.findByCriteria(criteria);
    }

    @Transactional(readOnly = true)
    public long countByCriteria(RefundPolicySearchCriteria criteria) {
        return queryPort.countByCriteria(criteria);
    }

    /**
     * 셀러의 활성 정책 개수 조회.
     *
     * <p>POL-DEACT-002: 마지막 활성 정책 비활성화 검증에 사용
     *
     * @param sellerId 셀러 ID
     * @return 활성 정책 개수
     */
    @Transactional(readOnly = true)
    public long countActiveBySellerId(SellerId sellerId) {
        return queryPort.countActiveBySellerId(sellerId);
    }
}
