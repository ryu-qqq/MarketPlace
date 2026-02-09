package com.ryuqq.marketplace.adapter.out.persistence.composite.seller.repository;

import static com.ryuqq.marketplace.adapter.out.persistence.refundpolicy.entity.QRefundPolicyJpaEntity.refundPolicyJpaEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.selleraddress.entity.QSellerAddressJpaEntity.sellerAddressJpaEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.shippingpolicy.entity.QShippingPolicyJpaEntity.shippingPolicyJpaEntity;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.composite.seller.condition.SellerOperationConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.composite.seller.condition.SellerPolicyConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.composite.seller.dto.SellerOperationCompositeDto;
import com.ryuqq.marketplace.adapter.out.persistence.composite.seller.dto.SellerOperationCompositeDto.AddressSummaryDto;
import com.ryuqq.marketplace.adapter.out.persistence.composite.seller.dto.SellerOperationCompositeDto.PolicySummaryDto;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 * SellerOperationCompositeQueryDslRepository - 셀러 운영 메타데이터 Composite 조회 Repository.
 *
 * <p>SellerAddress + ShippingPolicy + RefundPolicy 크로스 도메인 조회.
 */
@Repository
public class SellerOperationCompositeQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private final SellerOperationConditionBuilder operationConditionBuilder;
    private final SellerPolicyConditionBuilder policyConditionBuilder;

    public SellerOperationCompositeQueryDslRepository(
            JPAQueryFactory queryFactory,
            SellerOperationConditionBuilder operationConditionBuilder,
            SellerPolicyConditionBuilder policyConditionBuilder) {
        this.queryFactory = queryFactory;
        this.operationConditionBuilder = operationConditionBuilder;
        this.policyConditionBuilder = policyConditionBuilder;
    }

    public SellerOperationCompositeDto findBySellerId(Long sellerId) {
        List<AddressSummaryDto> addresses = fetchAddresses(sellerId);
        List<PolicySummaryDto> shippingPolicies = fetchShippingPolicies(sellerId);
        List<PolicySummaryDto> refundPolicies = fetchRefundPolicies(sellerId);

        return new SellerOperationCompositeDto(
                sellerId, addresses, shippingPolicies, refundPolicies);
    }

    private List<AddressSummaryDto> fetchAddresses(Long sellerId) {
        return queryFactory
                .select(
                        Projections.constructor(
                                AddressSummaryDto.class,
                                sellerAddressJpaEntity.addressType,
                                sellerAddressJpaEntity.defaultAddress))
                .from(sellerAddressJpaEntity)
                .where(
                        operationConditionBuilder.addressSellerIdEq(sellerId),
                        operationConditionBuilder.addressNotDeleted())
                .fetch();
    }

    private List<PolicySummaryDto> fetchShippingPolicies(Long sellerId) {
        return queryFactory
                .select(
                        Projections.constructor(
                                PolicySummaryDto.class, shippingPolicyJpaEntity.defaultPolicy))
                .from(shippingPolicyJpaEntity)
                .where(
                        policyConditionBuilder.shippingPolicySellerIdEq(sellerId),
                        policyConditionBuilder.shippingPolicyNotDeleted())
                .fetch();
    }

    private List<PolicySummaryDto> fetchRefundPolicies(Long sellerId) {
        return queryFactory
                .select(
                        Projections.constructor(
                                PolicySummaryDto.class, refundPolicyJpaEntity.defaultPolicy))
                .from(refundPolicyJpaEntity)
                .where(
                        policyConditionBuilder.refundPolicySellerIdEq(sellerId),
                        policyConditionBuilder.refundPolicyNotDeleted())
                .fetch();
    }
}
