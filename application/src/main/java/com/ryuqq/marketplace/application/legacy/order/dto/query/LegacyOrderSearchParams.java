package com.ryuqq.marketplace.application.legacy.order.dto.query;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 레거시 주문 목록 조회 검색 조건.
 *
 * @param orderStatusList 주문 상태 필터 (nullable)
 * @param lastDomainId 커서 기반 페이징용 마지막 주문 ID (nullable)
 * @param startDate 시작일 (nullable)
 * @param endDate 종료일 (nullable)
 * @param sellerId 셀러 ID (nullable)
 * @param size 조회 건수
 */
public record LegacyOrderSearchParams(
        List<String> orderStatusList,
        Long lastDomainId,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Long sellerId,
        int size) {}
