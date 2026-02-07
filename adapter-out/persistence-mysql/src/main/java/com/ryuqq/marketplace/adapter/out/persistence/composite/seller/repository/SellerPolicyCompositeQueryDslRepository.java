package com.ryuqq.marketplace.adapter.out.persistence.composite.seller.repository;

import static com.ryuqq.marketplace.adapter.out.persistence.refundpolicy.entity.QRefundPolicyJpaEntity.refundPolicyJpaEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.shippingpolicy.entity.QShippingPolicyJpaEntity.shippingPolicyJpaEntity;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.composite.seller.condition.SellerPolicyConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.composite.seller.dto.RefundPolicyDto;
import com.ryuqq.marketplace.adapter.out.persistence.composite.seller.dto.SellerPolicyCompositeDto;
import com.ryuqq.marketplace.adapter.out.persistence.composite.seller.dto.ShippingPolicyDto;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * SellerPolicyCompositeQueryDslRepository - 셀러 정책 Composite 조회 Repository.
 *
 * <p>ShippingPolicy + RefundPolicy 크로스 도메인 조회.
 *
 * <p>GetSellerForAdminService에서 사용.
 */
@Repository
public class SellerPolicyCompositeQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private final SellerPolicyConditionBuilder conditionBuilder;

    public SellerPolicyCompositeQueryDslRepository(
            JPAQueryFactory queryFactory, SellerPolicyConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    public Optional<SellerPolicyCompositeDto> findBySellerId(Long sellerId) {
        List<ShippingPolicyDto> shippingPolicies = fetchShippingPolicies(sellerId);
        List<RefundPolicyDto> refundPolicies = fetchRefundPolicies(sellerId);

        return Optional.of(
                new SellerPolicyCompositeDto(sellerId, shippingPolicies, refundPolicies));
    }

    private List<ShippingPolicyDto> fetchShippingPolicies(Long sellerId) {
        return queryFactory
                .select(
                        Projections.constructor(
                                ShippingPolicyDto.class,
                                shippingPolicyJpaEntity.id,
                                shippingPolicyJpaEntity.sellerId,
                                shippingPolicyJpaEntity.policyName,
                                shippingPolicyJpaEntity.defaultPolicy,
                                shippingPolicyJpaEntity.active,
                                shippingPolicyJpaEntity.shippingFeeType,
                                shippingPolicyJpaEntity.baseFee,
                                shippingPolicyJpaEntity.freeThreshold,
                                shippingPolicyJpaEntity.jejuExtraFee,
                                shippingPolicyJpaEntity.islandExtraFee,
                                shippingPolicyJpaEntity.returnFee,
                                shippingPolicyJpaEntity.exchangeFee,
                                shippingPolicyJpaEntity.leadTimeMinDays,
                                shippingPolicyJpaEntity.leadTimeMaxDays,
                                shippingPolicyJpaEntity.leadTimeCutoffTime,
                                shippingPolicyJpaEntity.createdAt,
                                shippingPolicyJpaEntity.updatedAt))
                .from(shippingPolicyJpaEntity)
                .where(
                        conditionBuilder.shippingPolicySellerIdEq(sellerId),
                        conditionBuilder.shippingPolicyNotDeleted())
                .fetch();
    }

    private List<RefundPolicyDto> fetchRefundPolicies(Long sellerId) {
        return queryFactory
                .select(
                        Projections.constructor(
                                RefundPolicyDto.class,
                                refundPolicyJpaEntity.id,
                                refundPolicyJpaEntity.sellerId,
                                refundPolicyJpaEntity.policyName,
                                refundPolicyJpaEntity.defaultPolicy,
                                refundPolicyJpaEntity.active,
                                refundPolicyJpaEntity.returnPeriodDays,
                                refundPolicyJpaEntity.exchangePeriodDays,
                                refundPolicyJpaEntity.nonReturnableConditions,
                                refundPolicyJpaEntity.partialRefundEnabled,
                                refundPolicyJpaEntity.inspectionRequired,
                                refundPolicyJpaEntity.inspectionPeriodDays,
                                refundPolicyJpaEntity.additionalInfo,
                                refundPolicyJpaEntity.createdAt,
                                refundPolicyJpaEntity.updatedAt))
                .from(refundPolicyJpaEntity)
                .where(
                        conditionBuilder.refundPolicySellerIdEq(sellerId),
                        conditionBuilder.refundPolicyNotDeleted())
                .fetch();
    }
}
