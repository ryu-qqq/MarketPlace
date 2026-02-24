package com.ryuqq.marketplace.adapter.out.persistence.productnotice.condition;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.marketplace.adapter.out.persistence.productnotice.entity.QProductNoticeJpaEntity;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * ProductNotice QueryDSL 조건 빌더.
 *
 * <p>PER-CND-001: BooleanExpression 조건을 null-safe static-like 메서드로 제공합니다.
 */
@Component
public class ProductNoticeConditionBuilder {

    private static final QProductNoticeJpaEntity productNotice =
            QProductNoticeJpaEntity.productNoticeJpaEntity;

    public BooleanExpression idEq(Long id) {
        return id != null ? productNotice.id.eq(id) : null;
    }

    public BooleanExpression productGroupIdEq(Long productGroupId) {
        return productGroupId != null ? productNotice.productGroupId.eq(productGroupId) : null;
    }

    public BooleanExpression productGroupIdIn(List<Long> productGroupIds) {
        return productGroupIds != null && !productGroupIds.isEmpty()
                ? productNotice.productGroupId.in(productGroupIds)
                : null;
    }
}
