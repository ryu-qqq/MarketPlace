package com.ryuqq.marketplace.adapter.out.persistence.composite.order.dto;

import java.util.List;

/** 주문 상세 Composite DTO (다중 쿼리 조합 결과). */
public record OrderDetailCompositeDto(
        OrderListProjectionDto order,
        List<OrderItemProjectionDto> items,
        List<OrderHistoryProjectionDto> histories,
        List<OrderCancelProjectionDto> cancels,
        List<OrderClaimProjectionDto> claims) {}
