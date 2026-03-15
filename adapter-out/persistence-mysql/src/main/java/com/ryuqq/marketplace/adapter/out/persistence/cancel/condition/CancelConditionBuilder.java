package com.ryuqq.marketplace.adapter.out.persistence.cancel.condition;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.marketplace.adapter.out.persistence.cancel.entity.QCancelJpaEntity;
import com.ryuqq.marketplace.domain.cancel.query.CancelDateField;
import com.ryuqq.marketplace.domain.cancel.query.CancelSearchCriteria;
import com.ryuqq.marketplace.domain.cancel.vo.CancelStatus;
import com.ryuqq.marketplace.domain.cancel.vo.CancelType;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Cancel QueryDSL 조건 빌더.
 *
 * <p>CancelSearchCriteria 기반의 동적 쿼리 조건을 생성합니다.
 */
@Component
public class CancelConditionBuilder {

    private static final QCancelJpaEntity cancel = QCancelJpaEntity.cancelJpaEntity;

    /**
     * ID 일치 조건.
     *
     * @param id 취소 ID
     * @return BooleanExpression (null이면 조건 무시)
     */
    public BooleanExpression idEq(String id) {
        return id != null ? cancel.id.eq(id) : null;
    }

    /**
     * 주문 ID 일치 조건.
     *
     * @param orderId 주문 ID
     * @return BooleanExpression (null이면 조건 무시)
     */
    public BooleanExpression orderIdEq(String orderId) {
        return orderId != null ? cancel.orderId.eq(orderId) : null;
    }

    /**
     * 주문 ID 목록 포함 조건.
     *
     * @param orderIds 주문 ID 목록
     * @return BooleanExpression (null이면 조건 무시)
     */
    public BooleanExpression orderIdIn(List<String> orderIds) {
        return orderIds != null && !orderIds.isEmpty() ? cancel.orderId.in(orderIds) : null;
    }

    /**
     * 취소 상태 필터 조건.
     *
     * @param criteria 검색 조건
     * @return BooleanExpression (null이면 조건 무시)
     */
    public BooleanExpression statusIn(CancelSearchCriteria criteria) {
        if (!criteria.hasStatusFilter()) {
            return null;
        }
        List<String> statusNames = criteria.statuses().stream().map(CancelStatus::name).toList();
        return cancel.cancelStatus.in(statusNames);
    }

    /**
     * 취소 유형 필터 조건.
     *
     * @param criteria 검색 조건
     * @return BooleanExpression (null이면 조건 무시)
     */
    public BooleanExpression typeIn(CancelSearchCriteria criteria) {
        if (!criteria.hasTypeFilter()) {
            return null;
        }
        List<String> typeNames = criteria.types().stream().map(CancelType::name).toList();
        return cancel.cancelType.in(typeNames);
    }

    /**
     * 검색어 조건.
     *
     * <p>검색 필드가 지정된 경우 해당 필드만, 미지정 시 cancelNumber로 검색합니다.
     *
     * @param criteria 검색 조건
     * @return BooleanExpression (null이면 조건 무시)
     */
    public BooleanExpression searchCondition(CancelSearchCriteria criteria) {
        if (!criteria.hasSearchCondition()) {
            return null;
        }
        String word = "%" + criteria.searchWord() + "%";
        if (!criteria.hasSearchField()) {
            return cancel.cancelNumber.like(word);
        }
        return switch (criteria.searchField()) {
            case CANCEL_NUMBER -> cancel.cancelNumber.like(word);
            case ORDER_NUMBER -> {
                String orderId =
                        criteria.searchWord() != null ? criteria.searchWord().trim() : null;
                yield orderId != null && !orderId.isBlank() ? cancel.orderId.eq(orderId) : null;
            }
            case CUSTOMER_NAME, CUSTOMER_PHONE, PRODUCT_NAME -> cancel.cancelNumber.like(word);
        };
    }

    /**
     * 날짜 범위 조건.
     *
     * <p>dateField에 따라 requestedAt 또는 completedAt을 기준으로 필터링합니다.
     *
     * @param criteria 검색 조건
     * @return BooleanExpression (null이면 조건 무시)
     */
    public BooleanExpression dateRange(CancelSearchCriteria criteria) {
        if (!criteria.hasDateRange()) {
            return null;
        }
        CancelDateField dateField =
                criteria.dateField() != null ? criteria.dateField() : CancelDateField.REQUESTED;

        var path =
                switch (dateField) {
                    case REQUESTED -> cancel.requestedAt;
                    case COMPLETED -> cancel.completedAt;
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
