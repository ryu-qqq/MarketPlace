package com.ryuqq.marketplace.adapter.out.persistence.productgroupdescription.condition;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.marketplace.adapter.out.persistence.productgroupdescription.entity.QProductGroupDescriptionJpaEntity;
import org.springframework.stereotype.Component;

/**
 * ProductGroupDescriptionConditionBuilder - 상품 그룹 상세설명 QueryDSL 조건 빌더.
 *
 * <p>PER-CND-001: BooleanExpression 조건을 null-safe로 제공.
 */
@Component
public class ProductGroupDescriptionConditionBuilder {

    private static final QProductGroupDescriptionJpaEntity description =
            QProductGroupDescriptionJpaEntity.productGroupDescriptionJpaEntity;

    public BooleanExpression idEq(Long id) {
        return id != null ? description.id.eq(id) : null;
    }

    public BooleanExpression productGroupIdEq(Long productGroupId) {
        return productGroupId != null ? description.productGroupId.eq(productGroupId) : null;
    }
}
