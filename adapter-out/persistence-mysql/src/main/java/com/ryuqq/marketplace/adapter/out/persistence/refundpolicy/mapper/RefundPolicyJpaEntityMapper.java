package com.ryuqq.marketplace.adapter.out.persistence.refundpolicy.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.refundpolicy.entity.RefundPolicyJpaEntity;
import com.ryuqq.marketplace.domain.refundpolicy.aggregate.RefundPolicy;
import com.ryuqq.marketplace.domain.refundpolicy.id.RefundPolicyId;
import com.ryuqq.marketplace.domain.refundpolicy.vo.NonReturnableCondition;
import com.ryuqq.marketplace.domain.refundpolicy.vo.RefundPolicyName;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * RefundPolicyJpaEntityMapper - 환불 정책 Entity-Domain 매퍼.
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
public class RefundPolicyJpaEntityMapper {

    private static final String CONDITION_DELIMITER = ",";

    /**
     * Domain → Entity 변환.
     *
     * @param domain RefundPolicy 도메인 객체
     * @return RefundPolicyJpaEntity
     */
    public RefundPolicyJpaEntity toEntity(RefundPolicy domain) {
        return RefundPolicyJpaEntity.create(
                domain.idValue(),
                domain.sellerIdValue(),
                domain.policyNameValue(),
                domain.isDefaultPolicy(),
                domain.isActive(),
                domain.returnPeriodDays(),
                domain.exchangePeriodDays(),
                toConditionsString(domain.nonReturnableConditions()),
                domain.isPartialRefundEnabled(),
                domain.isInspectionRequired(),
                domain.inspectionPeriodDays(),
                domain.additionalInfo(),
                domain.createdAt(),
                domain.updatedAt(),
                domain.deletedAt());
    }

    /**
     * Entity → Domain 변환.
     *
     * @param entity RefundPolicyJpaEntity
     * @return RefundPolicy 도메인 객체
     */
    public RefundPolicy toDomain(RefundPolicyJpaEntity entity) {
        var id =
                entity.getId() != null
                        ? RefundPolicyId.of(entity.getId())
                        : RefundPolicyId.forNew();
        return RefundPolicy.reconstitute(
                id,
                SellerId.of(entity.getSellerId()),
                RefundPolicyName.of(entity.getPolicyName()),
                entity.isDefaultPolicy(),
                entity.isActive(),
                entity.getReturnPeriodDays(),
                entity.getExchangePeriodDays(),
                toConditionsList(entity.getNonReturnableConditions()),
                entity.isPartialRefundEnabled(),
                entity.isInspectionRequired(),
                entity.getInspectionPeriodDays() != null ? entity.getInspectionPeriodDays() : 0,
                entity.getAdditionalInfo(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt());
    }

    private String toConditionsString(List<NonReturnableCondition> conditions) {
        if (conditions == null || conditions.isEmpty()) {
            return null;
        }
        return conditions.stream()
                .map(NonReturnableCondition::name)
                .collect(Collectors.joining(CONDITION_DELIMITER));
    }

    private List<NonReturnableCondition> toConditionsList(String conditionsString) {
        if (conditionsString == null || conditionsString.isBlank()) {
            return Collections.emptyList();
        }
        return Arrays.stream(conditionsString.split(CONDITION_DELIMITER))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(NonReturnableCondition::valueOf)
                .toList();
    }
}
