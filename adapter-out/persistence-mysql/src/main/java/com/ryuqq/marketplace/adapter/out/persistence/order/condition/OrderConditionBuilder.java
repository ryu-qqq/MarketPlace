package com.ryuqq.marketplace.adapter.out.persistence.order.condition;

import static com.ryuqq.marketplace.adapter.out.persistence.order.entity.QOrderJpaEntity.orderJpaEntity;

import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.stereotype.Component;

/** Order 단일 테이블 QueryDSL 조건 빌더. */
@Component
public class OrderConditionBuilder {

    public BooleanExpression idEq(String orderId) {
        return orderId != null ? orderJpaEntity.id.eq(orderId) : null;
    }

    public BooleanExpression orderNumberEq(String orderNumber) {
        return orderNumber != null ? orderJpaEntity.orderNumber.eq(orderNumber) : null;
    }

    public BooleanExpression salesChannelIdEq(long salesChannelId) {
        return orderJpaEntity.salesChannelId.eq(salesChannelId);
    }

    public BooleanExpression externalOrderNoEq(String externalOrderNo) {
        return externalOrderNo != null ? orderJpaEntity.externalOrderNo.eq(externalOrderNo) : null;
    }

    public BooleanExpression notDeleted() {
        return orderJpaEntity.deletedAt.isNull();
    }
}
