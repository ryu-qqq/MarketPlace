package com.ryuqq.marketplace.application.order.internal;

import com.ryuqq.marketplace.application.order.dto.response.OrderCancelResult;
import com.ryuqq.marketplace.application.order.dto.response.OrderClaimResult;
import com.ryuqq.marketplace.application.order.dto.response.OrderItemResult;
import com.ryuqq.marketplace.application.order.dto.response.OrderListResult;
import java.util.List;
import java.util.Map;

/**
 * 상품주문 리스트 번들 DTO.
 *
 * <p>ReadFacade에서 조회한 주문 아이템 기본 정보 + 취소/클레임 데이터를 묶어 Service로 전달합니다. Service는 이 번들을 Assembler에 넘겨 최종
 * {@link com.ryuqq.marketplace.application.order.dto.response.ProductOrderListResult}를 조립합니다.
 *
 * @param orderItems 주문 아이템 기본 정보 목록 (Composition 쿼리 결과)
 * @param ordersById 주문 아이템에 대응하는 주문 기본 정보 (orderId 키)
 * @param cancelsByItemId orderItemId별 취소 내역
 * @param claimsByItemId orderItemId별 클레임 내역
 * @param totalElements 전체 건수
 */
public record ProductOrderListBundle(
        List<OrderItemResult> orderItems,
        Map<String, OrderListResult> ordersById,
        Map<Long, List<OrderCancelResult>> cancelsByItemId,
        Map<Long, List<OrderClaimResult>> claimsByItemId,
        long totalElements) {}
