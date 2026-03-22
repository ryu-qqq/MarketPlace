package com.ryuqq.marketplace.application.legacy.order.dto.result;

import java.util.List;

/**
 * 레거시 주문 목록 페이징 결과.
 *
 * @param items 주문 상세 + 히스토리 목록
 * @param totalElements 전체 건수
 * @param lastDomainId 마지막 주문 ID (커서)
 */
public record LegacyOrderPageResult(
        List<LegacyOrderDetailWithHistoryResult> items, long totalElements, Long lastDomainId) {}
