package com.ryuqq.marketplace.application.shippingpolicy.manager;

import com.ryuqq.marketplace.application.shippingpolicy.port.out.query.ShippingPolicyQueryPort;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.shippingpolicy.aggregate.ShippingPolicy;
import com.ryuqq.marketplace.domain.shippingpolicy.exception.ShippingPolicyException;
import com.ryuqq.marketplace.domain.shippingpolicy.exception.ShippingPolicyNotFoundForSellerException;
import com.ryuqq.marketplace.domain.shippingpolicy.id.ShippingPolicyId;
import com.ryuqq.marketplace.domain.shippingpolicy.query.ShippingPolicySearchCriteria;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** 배송 정책 Read Manager. */
@Component
public class ShippingPolicyReadManager {

    private final ShippingPolicyQueryPort queryPort;

    public ShippingPolicyReadManager(ShippingPolicyQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    @Transactional(readOnly = true)
    public ShippingPolicy getById(ShippingPolicyId id) {
        return queryPort.findById(id).orElseThrow(ShippingPolicyException::policyNotFound);
    }

    @Transactional(readOnly = true)
    public List<ShippingPolicy> getByIds(List<ShippingPolicyId> ids) {
        return queryPort.findByIds(ids);
    }

    @Transactional(readOnly = true)
    public Optional<ShippingPolicy> findDefaultBySellerId(SellerId sellerId) {
        return queryPort.findDefaultBySellerId(sellerId);
    }

    @Transactional(readOnly = true)
    public ShippingPolicy getBySellerIdAndId(SellerId sellerId, ShippingPolicyId policyId) {
        return queryPort
                .findBySellerIdAndId(sellerId, policyId)
                .orElseThrow(
                        () ->
                                new ShippingPolicyNotFoundForSellerException(
                                        sellerId.value(), policyId.value()));
    }

    @Transactional(readOnly = true)
    public Optional<ShippingPolicy> findBySellerIdAndId(
            SellerId sellerId, ShippingPolicyId policyId) {
        return queryPort.findBySellerIdAndId(sellerId, policyId);
    }

    @Transactional(readOnly = true)
    public List<ShippingPolicy> findByCriteria(ShippingPolicySearchCriteria criteria) {
        return queryPort.findByCriteria(criteria);
    }

    @Transactional(readOnly = true)
    public long countByCriteria(ShippingPolicySearchCriteria criteria) {
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
