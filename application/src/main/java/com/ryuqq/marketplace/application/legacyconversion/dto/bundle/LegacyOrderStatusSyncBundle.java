package com.ryuqq.marketplace.application.legacyconversion.dto.bundle;

import com.ryuqq.marketplace.application.legacyconversion.internal.LegacyOrderStatusMapper;
import com.ryuqq.marketplace.domain.order.aggregate.Order;
import com.ryuqq.marketplace.domain.order.aggregate.OrderItem;
import com.ryuqq.marketplace.domain.order.vo.OrderItemStatus;

/**
 * 레거시 주문 상태 동기화 번들.
 *
 * <p>SyncCoordinator가 상태 비교 후 UpdateFacade에 전달하는 DTO입니다.
 *
 * @param order market DB의 Order
 * @param orderItem 상태 변경 대상 OrderItem (레거시 주문은 단일 아이템)
 * @param currentStatus 현재 market DB의 OrderItem 상태
 * @param resolution 레거시 상태에서 매핑된 목표 상태
 * @param legacyOrderId 레거시 주문 ID (로깅용)
 */
public record LegacyOrderStatusSyncBundle(
        Order order,
        OrderItem orderItem,
        OrderItemStatus currentStatus,
        LegacyOrderStatusMapper.OrderStatusResolution resolution,
        long legacyOrderId) {}
