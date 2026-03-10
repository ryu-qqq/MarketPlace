package com.ryuqq.marketplace.adapter.out.persistence.order.condition;

import static com.ryuqq.marketplace.adapter.out.persistence.order.entity.QOrderJpaEntity.orderJpaEntity;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.marketplace.domain.order.vo.OrderStatus;
import java.util.List;
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

    public BooleanExpression statusIn(List<OrderStatus> statuses) {
        if (statuses == null || statuses.isEmpty()) {
            return null;
        }
        List<String> statusNames = statuses.stream().map(OrderStatus::name).toList();
        return orderJpaEntity.status.in(statusNames);
    }

    public BooleanExpression notDeleted() {
        return orderJpaEntity.deletedAt.isNull();
    }
}
