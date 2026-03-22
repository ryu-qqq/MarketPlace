package com.ryuqq.marketplace.adapter.in.rest.legacy.order.dto.response;

/** 세토프 UpdateOrderResponse 호환 응답 DTO. */
public record LegacyUpdateOrderResponse(
        long orderId,
        long userId,
        String toBeOrderStatus,
        String asIsOrderStatus,
        String changeReason,
        String changeDetailReason) {}
