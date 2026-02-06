package com.ryuqq.marketplace.adapter.out.persistence.shippingpolicy.condition;

import static com.ryuqq.marketplace.adapter.out.persistence.shippingpolicy.entity.QShippingPolicyJpaEntity.shippingPolicyJpaEntity;

import com.querydsl.core.types.dsl.BooleanExpression;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * ShippingPolicyConditionBuilder - 배송 정책 QueryDSL 조건 빌더.
 *
 * <p>PER-CND-001: ConditionBuilder는 @Component로 등록.
 *
 * <p>PER-CND-002: 각 조건은 BooleanExpression 반환.
 *
 * <p>PER-CND-003: null 입력 시 null 반환 (동적 쿼리 지원).
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class ShippingPolicyConditionBuilder {

    /** ID 일치 조건 */
    public BooleanExpression idEq(Long id) {
        return id != null ? shippingPolicyJpaEntity.id.eq(id) : null;
    }

    /** ID 목록 포함 조건 */
    public BooleanExpression idIn(List<Long> ids) {
        return ids != null && !ids.isEmpty() ? shippingPolicyJpaEntity.id.in(ids) : null;
    }

    /** 셀러 ID 일치 조건 */
    public BooleanExpression sellerIdEq(Long sellerId) {
        return sellerId != null ? shippingPolicyJpaEntity.sellerId.eq(sellerId) : null;
    }

    /** 기본 정책 여부 일치 조건 */
    public BooleanExpression defaultPolicyEq(Boolean defaultPolicy) {
        return defaultPolicy != null
                ? shippingPolicyJpaEntity.defaultPolicy.eq(defaultPolicy)
                : null;
    }

    /** 활성화 상태 일치 조건 */
    public BooleanExpression activeEq(Boolean active) {
        return active != null ? shippingPolicyJpaEntity.active.eq(active) : null;
    }

    /** 삭제되지 않은 조건 */
    public BooleanExpression notDeleted() {
        return shippingPolicyJpaEntity.deletedAt.isNull();
    }
}
