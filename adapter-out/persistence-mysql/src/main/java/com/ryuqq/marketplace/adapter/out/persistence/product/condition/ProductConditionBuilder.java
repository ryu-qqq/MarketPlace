package com.ryuqq.marketplace.adapter.out.persistence.product.condition;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.marketplace.adapter.out.persistence.product.entity.QProductJpaEntity;
import java.util.List;
import org.springframework.stereotype.Component;

/** Product QueryDSL 조건 빌더. PER-CND-001: BooleanExpression 조건 반환. */
@Component
public class ProductConditionBuilder {

    private static final QProductJpaEntity product = QProductJpaEntity.productJpaEntity;

    public BooleanExpression idEq(Long id) {
        return id != null ? product.id.eq(id) : null;
    }

    public BooleanExpression idIn(List<Long> ids) {
        return ids != null && !ids.isEmpty() ? product.id.in(ids) : null;
    }

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
