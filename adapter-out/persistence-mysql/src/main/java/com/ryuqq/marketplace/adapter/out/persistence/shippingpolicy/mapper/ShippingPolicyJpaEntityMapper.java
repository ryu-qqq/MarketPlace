package com.ryuqq.marketplace.adapter.out.persistence.shippingpolicy.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.shippingpolicy.entity.ShippingPolicyJpaEntity;
import com.ryuqq.marketplace.domain.common.vo.Money;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.shippingpolicy.aggregate.ShippingPolicy;
import com.ryuqq.marketplace.domain.shippingpolicy.id.ShippingPolicyId;
import com.ryuqq.marketplace.domain.shippingpolicy.vo.LeadTime;
import com.ryuqq.marketplace.domain.shippingpolicy.vo.ShippingFeeType;
import com.ryuqq.marketplace.domain.shippingpolicy.vo.ShippingPolicyName;
import org.springframework.stereotype.Component;

/**
 * ShippingPolicyJpaEntityMapper - 배송 정책 Entity-Domain 매퍼.
 *
 * <p>Entity ↔ Domain 변환을 담당합니다.
 *
 * <p>PER-MAP-001: Mapper는 @Component로 등록.
 *
 * <p>PER-MAP-002: toEntity(Domain) + toDomain(Entity) 메서드 제공.
 *
 * <p>PER-MAP-003: 순수 변환 로직만.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class ShippingPolicyJpaEntityMapper {

    /**
     * Domain → Entity 변환.
     *
     * @param domain ShippingPolicy 도메인 객체
     * @return ShippingPolicyJpaEntity
     */
    public ShippingPolicyJpaEntity toEntity(ShippingPolicy domain) {
        return ShippingPolicyJpaEntity.create(
                domain.idValue(),
                domain.sellerIdValue(),
                domain.policyNameValue(),
                domain.isDefaultPolicy(),
                domain.isActive(),
                domain.shippingFeeType().name(),
                domain.baseFeeValue(),
                domain.freeThresholdValue(),
                domain.jejuExtraFeeValue(),
                domain.islandExtraFeeValue(),
                domain.returnFeeValue(),
                domain.exchangeFeeValue(),
                domain.leadTimeMinDays(),
                domain.leadTimeMaxDays(),
                domain.leadTimeCutoffTime(),
                domain.createdAt(),
                domain.updatedAt(),
                domain.deletedAt());
    }

    /**
     * Entity → Domain 변환.
     *
     * @param entity ShippingPolicyJpaEntity
     * @return ShippingPolicy 도메인 객체
     */
    public ShippingPolicy toDomain(ShippingPolicyJpaEntity entity) {
        var id =
                entity.getId() != null
                        ? ShippingPolicyId.of(entity.getId())
                        : ShippingPolicyId.forNew();
        var sellerId = SellerId.of(entity.getSellerId());
        return ShippingPolicy.reconstitute(
                id,
                sellerId,
                ShippingPolicyName.of(entity.getPolicyName()),
                entity.isDefaultPolicy(),
                entity.isActive(),
                ShippingFeeType.valueOf(entity.getShippingFeeType()),
                toMoney(entity.getBaseFee()),
                toMoney(entity.getFreeThreshold()),
                toMoney(entity.getJejuExtraFee()),
                toMoney(entity.getIslandExtraFee()),
                toMoney(entity.getReturnFee()),
                toMoney(entity.getExchangeFee()),
                toLeadTime(entity),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt());
    }

    private Money toMoney(Integer value) {
        return value != null ? Money.of(value) : null;
    }

    private LeadTime toLeadTime(ShippingPolicyJpaEntity entity) {
        if (entity.getLeadTimeMinDays() == null || entity.getLeadTimeMaxDays() == null) {
            return null;
        }
        return LeadTime.of(
                entity.getLeadTimeMinDays(),
                entity.getLeadTimeMaxDays(),
                entity.getLeadTimeCutoffTime());
    }
}
