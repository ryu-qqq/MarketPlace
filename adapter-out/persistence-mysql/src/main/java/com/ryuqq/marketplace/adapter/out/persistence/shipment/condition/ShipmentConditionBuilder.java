package com.ryuqq.marketplace.adapter.out.persistence.shipment.condition;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.marketplace.adapter.out.persistence.shipment.entity.QShipmentJpaEntity;
import com.ryuqq.marketplace.domain.shipment.query.ShipmentDateField;
import com.ryuqq.marketplace.domain.shipment.query.ShipmentSearchCriteria;
import com.ryuqq.marketplace.domain.shipment.vo.ShipmentStatus;
import java.util.List;
import org.springframework.stereotype.Component;

/** Shipment QueryDSL 조건 빌더. */
@Component
public class ShipmentConditionBuilder {

    private static final QShipmentJpaEntity shipment = QShipmentJpaEntity.shipmentJpaEntity;

    public BooleanExpression idEq(String id) {
        return id != null ? shipment.id.eq(id) : null;
    }

    public BooleanExpression orderIdEq(String orderId) {
        return orderId != null ? shipment.orderId.eq(orderId) : null;
    }

    public BooleanExpression statusIn(ShipmentSearchCriteria criteria) {
        if (!criteria.hasStatusFilter()) {
            return null;
        }
        List<String> statusNames = criteria.statuses().stream().map(ShipmentStatus::name).toList();
        return shipment.status.in(statusNames);
    }

    public BooleanExpression searchCondition(ShipmentSearchCriteria criteria) {
        if (!criteria.hasSearchCondition()) {
            return null;
        }
        String word = "%" + criteria.searchWord() + "%";
        if (!criteria.hasSearchField()) {
            return shipment.orderId
                    .like(word)
                    .or(shipment.trackingNumber.like(word))
                    .or(shipment.shipmentNumber.like(word));
        }
        return switch (criteria.searchField()) {
            case ORDER_ID -> shipment.orderId.like(word);
            case TRACKING_NUMBER -> shipment.trackingNumber.like(word);
            case CUSTOMER_NAME -> shipment.orderNumber.like(word);
        };
    }

    public BooleanExpression dateRange(ShipmentSearchCriteria criteria) {
        if (criteria.dateRange() == null || criteria.dateRange().isEmpty()) {
            return null;
        }
        ShipmentDateField dateField =
                criteria.dateField() != null ? criteria.dateField() : ShipmentDateField.SHIPPED;

        var path =
                switch (dateField) {
                    case PAYMENT -> shipment.createdAt;
                    case ORDER_CONFIRMED -> shipment.orderConfirmedAt;
                    case SHIPPED -> shipment.shippedAt;
                };

        BooleanExpression condition = null;
        if (criteria.dateRange().startInstant() != null) {
            condition = path.goe(criteria.dateRange().startInstant());
        }
        if (criteria.dateRange().endInstant() != null) {
            BooleanExpression endCondition = path.loe(criteria.dateRange().endInstant());
            condition = condition != null ? condition.and(endCondition) : endCondition;
        }
        return condition;
    }

    public BooleanExpression notDeleted() {
        return shipment.deletedAt.isNull();
    }
}
