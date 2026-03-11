package com.ryuqq.marketplace.application.order.internal;

import com.ryuqq.marketplace.application.order.dto.composite.ProductOrderDetailBundle;
import com.ryuqq.marketplace.application.order.dto.composite.ProductOrderDetailData;
import com.ryuqq.marketplace.application.order.dto.composite.ProductOrderListBundle;
import com.ryuqq.marketplace.application.order.dto.response.OrderCancelResult;
import com.ryuqq.marketplace.application.order.dto.response.OrderClaimResult;
import com.ryuqq.marketplace.application.order.dto.response.OrderHistoryResult;
import com.ryuqq.marketplace.application.order.dto.response.OrderItemResult;
import com.ryuqq.marketplace.application.order.dto.response.OrderListResult;
import com.ryuqq.marketplace.application.order.manager.OrderCompositionReadManager;
import com.ryuqq.marketplace.domain.order.exception.OrderNotFoundException;
import com.ryuqq.marketplace.domain.order.query.OrderSearchCriteria;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Order Read Facade.
 *
 * <p>ReadManager들만 조합하여 조회 결과를 번들 DTO로 묶어 반환합니다. 조립(Assembling)은 Service에서 Assembler를 통해 수행합니다.
 */
@Component
public class OrderReadFacade {

    private final OrderCompositionReadManager compositionReadManager;

    public OrderReadFacade(OrderCompositionReadManager compositionReadManager) {
        this.compositionReadManager = compositionReadManager;
    }

    /**
     * 상품주문(아이템) 단위 리스트 번들 조회.
     *
     * <p>1단계: 아이템 기준 Composition 쿼리 (필터 + 페이징) + 전체 건수
     *
     * <p>2단계: orderItemIds 추출 → 취소/클레임 IN 쿼리
     *
     * <p>3단계: orderId 추출 → 주문 기본 정보 IN 쿼리
     *
     * <p>4단계: 번들로 묶어 반환 (조립은 Service에서 Assembler가 수행)
     */
    @Transactional(readOnly = true)
    public ProductOrderListBundle getProductOrderListBundle(OrderSearchCriteria criteria) {
        List<OrderItemResult> orderItems = compositionReadManager.searchProductOrders(criteria);
        long totalElements = compositionReadManager.countProductOrders(criteria);

        if (orderItems.isEmpty()) {
            return new ProductOrderListBundle(
                    List.of(), Map.of(), Map.of(), Map.of(), totalElements);
        }

        List<Long> orderItemIds = orderItems.stream().map(OrderItemResult::orderItemId).toList();

        List<String> orderIds =
                orderItems.stream().map(OrderItemResult::orderId).distinct().toList();

        Map<String, OrderListResult> ordersById = compositionReadManager.findOrdersByIds(orderIds);

        Map<Long, List<OrderCancelResult>> cancelsByItemId =
                compositionReadManager.findCancelsByItemIds(orderItemIds);

        Map<Long, List<OrderClaimResult>> claimsByItemId =
                compositionReadManager.findClaimsByItemIds(orderItemIds);

        return new ProductOrderListBundle(
                orderItems, ordersById, cancelsByItemId, claimsByItemId, totalElements);
    }

    /**
     * 상품주문 상세 번들 조회.
     *
     * <p>1단계: orderItemId로 아이템 단건 조회 (item + order + payment + settlement)
     *
     * <p>2단계: 해당 아이템의 취소/클레임 조회
     *
     * <p>3단계: orderId로 주문 타임라인 조회
     */
    @Transactional(readOnly = true)
    public ProductOrderDetailBundle getProductOrderDetailBundle(long orderItemId) {
        ProductOrderDetailData data =
                compositionReadManager
                        .findProductOrderDetail(orderItemId)
                        .orElseThrow(() -> new OrderNotFoundException(String.valueOf(orderItemId)));

        List<OrderCancelResult> cancels =
                compositionReadManager.findCancelsByOrderItemId(orderItemId);

        List<OrderClaimResult> claims = compositionReadManager.findClaimsByOrderItemId(orderItemId);

        List<OrderHistoryResult> histories =
                compositionReadManager.findHistoriesByOrderId(data.item().orderId());

        return new ProductOrderDetailBundle(
                data.item(), data.order(), data.payment(), cancels, claims, histories);
    }
}
