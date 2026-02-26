package com.ryuqq.marketplace.adapter.out.persistence.composite.seller.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.composite.seller.dto.RefundPolicyDto;
import com.ryuqq.marketplace.adapter.out.persistence.composite.seller.dto.SellerPolicyCompositeDto;
import com.ryuqq.marketplace.adapter.out.persistence.composite.seller.dto.ShippingPolicyDto;
import com.ryuqq.marketplace.adapter.out.persistence.composite.seller.mapper.SellerCompositeMapper;
import com.ryuqq.marketplace.adapter.out.persistence.composite.seller.repository.SellerCompositeQueryDslRepository;
import com.ryuqq.marketplace.adapter.out.persistence.composite.seller.repository.SellerPolicyCompositeQueryDslRepository;
import com.ryuqq.marketplace.application.seller.dto.composite.SellerAdminCompositeResult;
import com.ryuqq.marketplace.application.seller.dto.composite.SellerCompositeResult;
import com.ryuqq.marketplace.application.seller.dto.composite.SellerPolicyCompositeResult;
import com.ryuqq.marketplace.application.seller.port.out.query.SellerCompositionQueryPort;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * SellerCompositionQueryAdapter - 셀러 Composition 조회 Adapter.
 *
 * <p>크로스 도메인 조인을 통한 성능 최적화된 조회 구현.
 *
 * <p>PER-ADP-001: Adapter는 @Component로 등록.
 *
 * <p>PER-ADP-002: Port 인터페이스 구현.
 */
@Component
public class SellerCompositionQueryAdapter implements SellerCompositionQueryPort {

    private final SellerCompositeQueryDslRepository compositeRepository;
    private final SellerPolicyCompositeQueryDslRepository policyCompositeRepository;
    private final SellerCompositeMapper compositeMapper;

    public SellerCompositionQueryAdapter(
            SellerCompositeQueryDslRepository compositeRepository,
            SellerPolicyCompositeQueryDslRepository policyCompositeRepository,
            SellerCompositeMapper compositeMapper) {
        this.compositeRepository = compositeRepository;
        this.policyCompositeRepository = policyCompositeRepository;
        this.compositeMapper = compositeMapper;
    }

    @Override
    public Optional<SellerCompositeResult> findSellerCompositeById(Long sellerId) {
        return compositeRepository.findBySellerId(sellerId).map(compositeMapper::toResult);
    }

    @Override
    public Optional<SellerAdminCompositeResult> findAdminCompositeById(Long sellerId) {
        return compositeRepository
                .findAdminCompositeById(sellerId)
                .map(compositeMapper::toAdminResult);
    }

    @Override
    public Optional<SellerAdminCompositeResult> findAdminCompositeByAuthTenantId(
            String authTenantId) {
        return compositeRepository
                .findAdminCompositeByAuthTenantId(authTenantId)
                .map(compositeMapper::toAdminResult);
    }

    @Override
    public Optional<SellerPolicyCompositeResult> findPolicyCompositeById(Long sellerId) {
        return policyCompositeRepository.findBySellerId(sellerId).map(this::toResult);
    }

    private SellerPolicyCompositeResult toResult(SellerPolicyCompositeDto dto) {
        return new SellerPolicyCompositeResult(
                dto.sellerId(),
                dto.shippingPolicies().stream().map(this::toShippingPolicyInfo).toList(),
                dto.refundPolicies().stream().map(this::toRefundPolicyInfo).toList());
    }

    private SellerPolicyCompositeResult.ShippingPolicyInfo toShippingPolicyInfo(
            ShippingPolicyDto dto) {
        return new SellerPolicyCompositeResult.ShippingPolicyInfo(
                dto.id(),
                dto.sellerId(),
                dto.policyName(),
                dto.defaultPolicy(),
                dto.active(),
                dto.shippingFeeType(),
                dto.baseFee(),
                dto.freeThreshold(),
                dto.jejuExtraFee(),
                dto.islandExtraFee(),
                dto.returnFee(),
                dto.exchangeFee(),
                dto.leadTimeMinDays(),
                dto.leadTimeMaxDays(),
                dto.leadTimeCutoffTime(),
                dto.createdAt(),
                dto.updatedAt());
    }

    private SellerPolicyCompositeResult.RefundPolicyInfo toRefundPolicyInfo(RefundPolicyDto dto) {
        return new SellerPolicyCompositeResult.RefundPolicyInfo(
                dto.id(),
                dto.sellerId(),
                dto.policyName(),
                dto.defaultPolicy(),
                dto.active(),
                dto.returnPeriodDays(),
                dto.exchangePeriodDays(),
                dto.nonReturnableConditions(),
                dto.partialRefundEnabled(),
                dto.inspectionRequired(),
                dto.inspectionPeriodDays(),
                dto.additionalInfo(),
                dto.createdAt(),
                dto.updatedAt());
    }
}
