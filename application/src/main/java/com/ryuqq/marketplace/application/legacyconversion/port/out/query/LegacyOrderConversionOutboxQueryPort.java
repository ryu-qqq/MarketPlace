package com.ryuqq.marketplace.application.legacyconversion.port.out.query;

import com.ryuqq.marketplace.domain.legacyconversion.aggregate.LegacyOrderConversionOutbox;
import java.time.Instant;
import java.util.List;

/** 레거시 주문 변환 Outbox 조회 포트. */
public interface LegacyOrderConversionOutboxQueryPort {

    /**
     * 처리 대기 중인 Outbox 목록 조회.
     *
     * @param beforeTime 이 시간 이전에 생성된 것만 조회
     * @param limit 최대 조회 개수
     * @return Outbox 목록
     */
    List<LegacyOrderConversionOutbox> findPendingOutboxes(Instant beforeTime, int limit);

    /**
     * PROCESSING 타임아웃 Outbox 목록 조회.
     *
     * @param timeoutThreshold 이 시간 이전에 업데이트된 것만 조회
     * @param limit 최대 조회 개수
     * @return Outbox 목록
     */
    List<LegacyOrderConversionOutbox> findProcessingTimeoutOutboxes(
            Instant timeoutThreshold, int limit);

    /**
     * 해당 legacyOrderId에 대한 Outbox가 존재하는지 확인.
     *
     * @param legacyOrderId 레거시 주문 ID
     * @return 존재 여부
     */
    boolean existsByLegacyOrderId(long legacyOrderId);
}
