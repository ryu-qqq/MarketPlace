package com.ryuqq.marketplace.application.order.assembler;

import com.ryuqq.marketplace.application.order.dto.response.OrderDetailResult;
import com.ryuqq.marketplace.application.order.dto.response.OrderDetailResult.BuyerInfoResult;
import com.ryuqq.marketplace.application.order.dto.response.OrderHistoryResult;
import com.ryuqq.marketplace.application.order.dto.response.OrderItemResult;
import com.ryuqq.marketplace.application.order.dto.response.OrderListResult;
import com.ryuqq.marketplace.application.order.dto.response.OrderPageResult;
import com.ryuqq.marketplace.application.order.dto.response.OrderSummaryResult;
import com.ryuqq.marketplace.domain.common.vo.PageMeta;
import com.ryuqq.marketplace.domain.order.aggregate.Order;
import com.ryuqq.marketplace.domain.order.aggregate.OrderHistory;
import com.ryuqq.marketplace.domain.order.aggregate.OrderItem;
import com.ryuqq.marketplace.domain.order.vo.BuyerInfo;
import com.ryuqq.marketplace.domain.order.vo.OrderStatus;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * Order Assembler.
 *
 * <p>Domain → Result 변환 및 PageResult 생성을 담당합니다.
 */
@Component
public class OrderAssembler {

    /**
     * Domain → OrderListResult 변환.
     *
     * @param order Order 도메인 객체
     * @return OrderListResult
     */
    public OrderListResult toListResult(Order order) {
        return new OrderListResult(
                order.idValue(),
                order.orderNumberValue(),
                order.status().name(),
                order.externalOrderReference().salesChannelId(),
                order.externalOrderReference().externalOrderNo(),
                order.buyerInfo().buyerName().value(),
                order.items().size(),
                order.orderedAt(),
                order.createdAt());
    }

    /**
     * Domain List → OrderListResult List 변환.
     *
     * @param orders Order 도메인 객체 목록
     * @return OrderListResult 목록
     */
    public List<OrderListResult> toListResults(List<Order> orders) {
        return orders.stream().map(this::toListResult).toList();
    }

    /**
     * Domain → OrderDetailResult 변환.
     *
     * @param order Order 도메인 객체
     * @return OrderDetailResult
     */
    public OrderDetailResult toDetailResult(Order order) {
        BuyerInfoResult buyerInfoResult = toBuyerInfoResult(order.buyerInfo());
        List<OrderItemResult> itemResults = toItemResults(order.items());
        List<OrderHistoryResult> historyResults = toHistoryResults(order.histories());

        return new OrderDetailResult(
                order.idValue(),
                order.orderNumberValue(),
                order.status().name(),
                order.externalOrderReference().salesChannelId(),
                order.externalOrderReference().shopId(),
                order.externalOrderReference().externalOrderNo(),
                order.externalOrderReference().externalOrderedAt(),
                buyerInfoResult,
                itemResults,
                historyResults,
                order.orderedAt(),
                order.createdAt(),
                order.updatedAt());
    }

    /**
     * 페이지 결과 생성.
     *
     * @param orders Order 도메인 객체 목록
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @param totalCount 전체 개수
     * @return OrderPageResult
     */
    public OrderPageResult toPageResult(List<Order> orders, int page, int size, long totalCount) {
        List<OrderListResult> results = toListResults(orders);
        PageMeta pageMeta = PageMeta.of(page, size, totalCount);
        return new OrderPageResult(results, pageMeta);
    }

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

    private OrderItemResult toItemResult(OrderItem item) {
        return new OrderItemResult(
                item.idValue(),
                item.internalProduct().productGroupId(),
                item.internalProduct().productId(),
                item.internalProduct().skuCode(),
                item.externalProduct().externalProductId(),
                item.externalProduct().externalProductName(),
                item.externalProduct().externalOptionName(),
                item.externalProduct().externalImageUrl(),
                item.price().unitPrice().value(),
                item.price().quantity(),
                item.price().paymentAmount().value(),
                item.receiverInfo().receiverName());
    }

    private List<OrderItemResult> toItemResults(List<OrderItem> items) {
        return items.stream().map(this::toItemResult).toList();
    }

    private OrderHistoryResult toHistoryResult(OrderHistory history) {
        return new OrderHistoryResult(
                history.idValue(),
                history.fromStatus() != null ? history.fromStatus().name() : null,
                history.toStatus().name(),
                history.changedBy(),
                history.reason(),
                history.changedAt());
    }

    private List<OrderHistoryResult> toHistoryResults(List<OrderHistory> histories) {
        return histories.stream().map(this::toHistoryResult).toList();
    }

    private BuyerInfoResult toBuyerInfoResult(BuyerInfo buyerInfo) {
        return new BuyerInfoResult(
                buyerInfo.buyerName().value(),
                buyerInfo.email() != null ? buyerInfo.email().value() : null,
                buyerInfo.phoneNumber() != null ? buyerInfo.phoneNumber().value() : null);
    }
}
