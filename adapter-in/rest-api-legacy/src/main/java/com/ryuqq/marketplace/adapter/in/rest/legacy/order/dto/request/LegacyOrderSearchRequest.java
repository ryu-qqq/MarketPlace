package com.ryuqq.marketplace.adapter.in.rest.legacy.order.dto.request;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 세토프 주문 목록 조회 요청 DTO.
 *
 * @param orderStatusList 주문 상태 필터 (nullable)
 * @param lastDomainId 커서 기반 페이징용 마지막 주문 ID (nullable)
 * @param startDate 시작일 (nullable)
 * @param endDate 종료일 (nullable)
 * @param sellerId 셀러 ID (nullable)
 * @param size 조회 건수 (기본 20)
 */
public record LegacyOrderSearchRequest(
        List<String> orderStatusList,
        Long lastDomainId,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Long sellerId,
        Integer size) {

    public int resolvedSize() {
        return size != null && size > 0 ? size : 20;
    }
}
