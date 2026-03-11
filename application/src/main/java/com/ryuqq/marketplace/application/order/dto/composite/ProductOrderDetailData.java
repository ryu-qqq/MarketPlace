package com.ryuqq.marketplace.application.order.dto.composite;

import com.ryuqq.marketplace.application.order.dto.response.OrderItemResult;
import com.ryuqq.marketplace.application.order.dto.response.OrderListResult;
import com.ryuqq.marketplace.application.order.dto.response.PaymentResult;

/**
 * 상품주문 상세 복합 조회 결과.
 *
 * <p>단건 상세 조회 시 orders + order_items + payments JOIN 결과를 하나로 묶어 반환합니다. 한 번의 쿼리로 아이템, 주문, 결제 정보를 모두
 * 추출합니다.
 *
 * @param item 상품주문 정보 (item + receiver + delivery + settlement)
 * @param order 주문 기본 정보 (orderId, orderNumber, buyerName 등)
 * @param payment 결제 정보 (7필드, nullable)
 */
public record ProductOrderDetailData(
        OrderItemResult item, OrderListResult order, PaymentResult payment) {}
