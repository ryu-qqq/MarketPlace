package com.ryuqq.marketplace.application.legacy.order.dto.result;

import java.util.List;

/**
 * 레거시 주문 상세 + 히스토리 묶음 (목록 조회용).
 *
 * @param order 주문 상세
 * @param histories 주문 이력 목록
 */
public record LegacyOrderDetailWithHistoryResult(
        LegacyOrderDetailResult order, List<LegacyOrderHistoryResult> histories) {}
