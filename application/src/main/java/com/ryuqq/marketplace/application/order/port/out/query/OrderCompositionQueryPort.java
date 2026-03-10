package com.ryuqq.marketplace.application.order.port.out.query;

import com.ryuqq.marketplace.application.order.dto.composite.ProductOrderDetailData;
import com.ryuqq.marketplace.application.order.dto.response.OrderCancelResult;
import com.ryuqq.marketplace.application.order.dto.response.OrderClaimResult;
import com.ryuqq.marketplace.application.order.dto.response.OrderDetailResult;
import com.ryuqq.marketplace.application.order.dto.response.OrderHistoryResult;
import com.ryuqq.marketplace.application.order.dto.response.OrderItemResult;
import com.ryuqq.marketplace.application.order.dto.response.OrderListResult;
import com.ryuqq.marketplace.domain.order.query.OrderSearchCriteria;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** 주문 Composition 조회 Port. 크로스 테이블 조인을 통한 성능 최적화된 조회. */
public interface OrderCompositionQueryPort {

    List<OrderListResult> searchOrders(OrderSearchCriteria criteria);

    long countOrders(OrderSearchCriteria criteria);

    Optional<OrderDetailResult> findOrderDetail(String orderId);

    // ==================== V5 상품주문 리스트 ====================

    /**
     * 상품주문(아이템) 단위 리스트 조회.
     *
     * <p>order_item 테이블 기준 페이징하며, 주문/결제/배송 정보를 JOIN하여 반환합니다.
     *
     * @param criteria 검색 조건
     * @return 아이템 단위 조회 결과
     */
    List<OrderItemResult> searchProductOrders(OrderSearchCriteria criteria);

    /**
     * 상품주문(아이템) 단위 전체 건수 카운트.
     *
     * @param criteria 검색 조건
     * @return 전체 건수
     */
    long countProductOrders(OrderSearchCriteria criteria);

    /**
     * orderItemId 목록에 해당하는 주문 기본 정보 조회.
     *
     * <p>orderId 기준으로 중복 제거하여 반환합니다.
     *
     * @param orderIds 주문 ID 목록
     * @return orderId 키의 주문 기본 정보 맵
     */
    Map<String, OrderListResult> findOrdersByIds(List<String> orderIds);

    /**
     * orderItemId별 취소 내역 조회.
     *
     * @param orderItemIds 주문 상품 ID 목록
     * @return orderItemId 키의 취소 결과 맵
     */
    Map<Long, List<OrderCancelResult>> findCancelsByItemIds(List<Long> orderItemIds);

    /**
     * orderItemId별 클레임 내역 조회.
     *
     * @param orderItemIds 주문 상품 ID 목록
     * @return orderItemId 키의 클레임 결과 맵
     */
    Map<Long, List<OrderClaimResult>> findClaimsByItemIds(List<Long> orderItemIds);

    // ==================== V5 상품주문 상세 ====================

    /** 상품주문 상세 단건 조회 (item + order + payment를 한 쿼리로 조회). */
    Optional<ProductOrderDetailData> findProductOrderDetail(long orderItemId);

    /** 상품주문 단건 취소 목록 조회. */
    List<OrderCancelResult> findCancelsByOrderItemId(long orderItemId);

    /** 상품주문 단건 클레임 목록 조회. */
    List<OrderClaimResult> findClaimsByOrderItemId(long orderItemId);

    /** 주문 타임라인 조회. */
    List<OrderHistoryResult> findHistoriesByOrderId(String orderId);
}
