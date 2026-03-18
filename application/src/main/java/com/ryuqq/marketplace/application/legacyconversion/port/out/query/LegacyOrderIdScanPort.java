package com.ryuqq.marketplace.application.legacyconversion.port.out.query;

import java.util.List;

/**
 * 레거시 주문 ID 커서 기반 스캔 Port.
 *
 * <p>orders 테이블에서 활성 주문 ID와 결제 ID를 커서 기반으로 페이징 조회합니다.
 */
public interface LegacyOrderIdScanPort {

    /**
     * 지정된 ID 이후의 활성 주문 엔트리(orderId + paymentId) 목록을 조회합니다.
     *
     * @param afterId 이 ID 이후부터 조회 (exclusive)
     * @param limit 최대 조회 개수
     * @return 활성 주문 스캔 엔트리 목록 (orderId 오름차순)
     */
    List<LegacyOrderScanEntry> findActiveOrderEntries(long afterId, int limit);
}
