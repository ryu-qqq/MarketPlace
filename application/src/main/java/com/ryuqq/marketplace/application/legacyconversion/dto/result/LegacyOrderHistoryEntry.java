package com.ryuqq.marketplace.application.legacyconversion.dto.result;

import java.time.Instant;

/**
 * 레거시 주문 이력 엔트리.
 *
 * <p>orders_history 테이블의 상태 변경 이력 한 건을 표현합니다. 컨버전 시 실제 타임스탬프 및 취소/반품 사유를 추출하는 데 사용됩니다.
 *
 * @param orderStatus 변경된 주문 상태
 * @param changeReason 변경 사유 (예: "단순 변심", "재고부족")
 * @param changeDetailReason 변경 상세 사유
 * @param changedAt 상태 변경 시각
 */
public record LegacyOrderHistoryEntry(
        String orderStatus, String changeReason, String changeDetailReason, Instant changedAt) {}
