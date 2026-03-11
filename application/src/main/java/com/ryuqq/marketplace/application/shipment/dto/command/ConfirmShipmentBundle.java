package com.ryuqq.marketplace.application.shipment.dto.command;

import com.ryuqq.marketplace.domain.order.aggregate.OrderItem;
import com.ryuqq.marketplace.domain.shipment.aggregate.Shipment;
import com.ryuqq.marketplace.domain.shipment.outbox.aggregate.ShipmentOutbox;
import java.util.List;

/**
 * 발주확인 번들 DTO.
 *
 * <p>Shipment 생성 + ShipmentOutbox 생성 + OrderItem 상태변경을 한 트랜잭션에서 처리하기 위한 번들.
 *
 * @param shipments 생성된 배송 목록
 * @param outboxes 생성된 아웃박스 목록
 * @param orderItems 상태 변경된 주문상품 목록
 */
public record ConfirmShipmentBundle(
        List<Shipment> shipments, List<ShipmentOutbox> outboxes, List<OrderItem> orderItems) {}
