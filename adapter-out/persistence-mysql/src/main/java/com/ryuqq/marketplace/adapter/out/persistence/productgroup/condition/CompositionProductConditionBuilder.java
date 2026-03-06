package com.ryuqq.marketplace.adapter.out.persistence.productgroup.condition;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.marketplace.adapter.out.persistence.product.entity.QProductJpaEntity;
import java.util.List;
import org.springframework.stereotype.Component;

/** Composition Repository 전용 Product 조건 빌더. */
@Component
public class CompositionProductConditionBuilder {

    private static final QProductJpaEntity product = QProductJpaEntity.productJpaEntity;

    public BooleanExpression productGroupIdEq(Long productGroupId) {
        return productGroupId != null ? product.productGroupId.eq(productGroupId) : null;
    }

    public BooleanExpression productGroupIdIn(List<Long> productGroupIds) {
        return productGroupIds != null && !productGroupIds.isEmpty()
                ? product.productGroupId.in(productGroupIds)
                : null;
    }

    public BooleanExpression statusNotDeleted() {
        return product.status.ne("DELETED");
    }
}
