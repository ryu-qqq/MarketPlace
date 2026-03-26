package com.ryuqq.marketplace.application.order.internal;

import com.ryuqq.marketplace.application.order.dto.response.OrderCancelResult;
import com.ryuqq.marketplace.application.order.dto.response.OrderClaimResult;
import com.ryuqq.marketplace.application.order.dto.response.OrderHistoryResult;
import com.ryuqq.marketplace.application.order.dto.response.OrderItemResult;
import com.ryuqq.marketplace.application.order.dto.response.OrderListResult;
import com.ryuqq.marketplace.application.order.dto.response.PaymentResult;
import java.util.List;

/**
 * 상품주문 상세 번들 DTO.
 *
 * <p>ReadFacade에서 조회한 상세 데이터를 묶어 Assembler에 전달합니다.
 *
 * @param item 상품주문 정보 (item + receiver + delivery + settlement)
 * @param order 주문 기본 정보 (orderId, orderNumber, buyerName 등)
 * @param payment 결제 정보 (7필드)
 * @param cancels 취소 목록
 * @param claims 클레임 목록
 * @param histories 주문 타임라인
 */
public record ProductOrderDetailBundle(
        OrderItemResult item,
        OrderListResult order,
        PaymentResult payment,
        List<OrderCancelResult> cancels,
        List<OrderClaimResult> claims,
        List<OrderHistoryResult> histories) {}
