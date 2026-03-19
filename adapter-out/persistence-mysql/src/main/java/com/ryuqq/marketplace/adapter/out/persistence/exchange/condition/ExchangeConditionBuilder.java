package com.ryuqq.marketplace.adapter.out.persistence.exchange.condition;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.marketplace.adapter.out.persistence.exchange.entity.QExchangeClaimJpaEntity;
import com.ryuqq.marketplace.domain.exchange.query.ExchangeDateField;
import com.ryuqq.marketplace.domain.exchange.query.ExchangeSearchCriteria;
import com.ryuqq.marketplace.domain.exchange.vo.ExchangeStatus;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Exchange QueryDSL 조건 빌더.
 *
 * <p>ExchangeSearchCriteria 기반의 동적 쿼리 조건을 생성합니다.
 */
@Component
public class ExchangeConditionBuilder {

    private static final QExchangeClaimJpaEntity exchange =
            QExchangeClaimJpaEntity.exchangeClaimJpaEntity;

    public BooleanExpression idEq(String id) {
        return id != null ? exchange.id.eq(id) : null;
    }

    public BooleanExpression idIn(List<String> ids) {
        return ids != null && !ids.isEmpty() ? exchange.id.in(ids) : null;
    }

    public BooleanExpression sellerIdEq(Long sellerId) {
        return sellerId != null ? exchange.sellerId.eq(sellerId) : null;
    }

    public BooleanExpression orderItemIdEq(String orderItemId) {
        return exchange.orderItemId.eq(orderItemId);
    }

    public BooleanExpression orderItemIdIn(List<String> orderItemIds) {
        return orderItemIds != null && !orderItemIds.isEmpty()
                ? exchange.orderItemId.in(orderItemIds)
                : null;
    }

    public BooleanExpression statusIn(ExchangeSearchCriteria criteria) {
        if (!criteria.hasStatusFilter()) {
            return null;
        }
        List<String> statusNames = criteria.statuses().stream().map(ExchangeStatus::name).toList();
        return exchange.exchangeStatus.in(statusNames);
    }

    public BooleanExpression searchCondition(ExchangeSearchCriteria criteria) {
        if (!criteria.hasSearchCondition()) {
            return null;
        }
        String word = "%" + criteria.searchWord() + "%";
        if (!criteria.hasSearchField()) {
            return exchange.claimNumber.like(word);
        }
        return switch (criteria.searchField()) {
            case CLAIM_NUMBER -> exchange.claimNumber.like(word);
            case ORDER_NUMBER -> exchange.claimNumber.like(word);
            case CUSTOMER_NAME, CUSTOMER_PHONE, PRODUCT_NAME -> exchange.claimNumber.like(word);
        };
    }

    public BooleanExpression dateRange(ExchangeSearchCriteria criteria) {
        if (!criteria.hasDateRange()) {
            return null;
        }
        ExchangeDateField dateField =
                criteria.dateField() != null ? criteria.dateField() : ExchangeDateField.REQUESTED;

        var path =
                switch (dateField) {
                    case REQUESTED -> exchange.requestedAt;
                    case COMPLETED -> exchange.completedAt;
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
}
