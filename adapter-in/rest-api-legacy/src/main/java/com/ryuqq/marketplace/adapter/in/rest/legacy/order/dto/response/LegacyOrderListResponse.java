package com.ryuqq.marketplace.adapter.in.rest.legacy.order.dto.response;

import java.util.List;

/**
 * 세토프 OrderListResponse 호환 응답 DTO.
 *
 * @param order 주문 상세
 * @param histories 주문 이력 목록
 */
public record LegacyOrderListResponse(
        LegacyOrderResponse order, List<LegacyOrderHistoryResponse> histories) {}
