package com.ryuqq.marketplace.application.order.assembler;

import com.ryuqq.marketplace.application.order.dto.response.OrderSummaryResult;
import com.ryuqq.marketplace.domain.order.vo.OrderStatus;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * Order Assembler.
 *
 * <p>Domain → Result 변환을 담당합니다. 목록/상세 조회는 Composite 경로로 전환되어 OrderCompositeMapper에서 처리합니다.
 */
@Component
public class OrderAssembler {

    /**
     * 상태별 카운트 → OrderSummaryResult 변환.
     *
     * @param statusCounts 상태별 카운트 맵
     * @return OrderSummaryResult
     */
    public OrderSummaryResult toSummaryResult(Map<OrderStatus, Long> statusCounts) {
        return new OrderSummaryResult(
                statusCounts.getOrDefault(OrderStatus.ORDERED, 0L).intValue(),
                statusCounts.getOrDefault(OrderStatus.PREPARING, 0L).intValue(),
                statusCounts.getOrDefault(OrderStatus.SHIPPED, 0L).intValue(),
                statusCounts.getOrDefault(OrderStatus.DELIVERED, 0L).intValue(),
                statusCounts.getOrDefault(OrderStatus.CONFIRMED, 0L).intValue(),
                statusCounts.getOrDefault(OrderStatus.CANCELLED, 0L).intValue(),
                statusCounts.getOrDefault(OrderStatus.CLAIM_IN_PROGRESS, 0L).intValue(),
                statusCounts.getOrDefault(OrderStatus.REFUNDED, 0L).intValue(),
                statusCounts.getOrDefault(OrderStatus.EXCHANGED, 0L).intValue());
    }
}
