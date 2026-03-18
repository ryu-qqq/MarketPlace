package com.ryuqq.marketplace.application.legacyconversion.manager;

import com.ryuqq.marketplace.application.legacyconversion.port.out.query.LegacyOrderConversionOutboxQueryPort;
import com.ryuqq.marketplace.domain.legacyconversion.aggregate.LegacyOrderConversionOutbox;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** 레거시 주문 변환 Outbox 조회 Manager. */
@Component
@Transactional(readOnly = true)
public class LegacyOrderConversionOutboxReadManager {

    private final LegacyOrderConversionOutboxQueryPort queryPort;

    public LegacyOrderConversionOutboxReadManager(
            LegacyOrderConversionOutboxQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    /**
     * 처리 대기 중인 Outbox 목록 조회.
     *
     * @param beforeTime 이 시간 이전에 생성된 것만 조회
     * @param limit 최대 조회 개수
     * @return Outbox 목록
     */
    public List<LegacyOrderConversionOutbox> findPendingOutboxes(Instant beforeTime, int limit) {
        return queryPort.findPendingOutboxes(beforeTime, limit);
    }

    /**
     * PROCESSING 타임아웃 Outbox 목록 조회.
     *
     * @param timeoutThreshold 이 시간 이전에 업데이트된 것만 조회
     * @param limit 최대 조회 개수
     * @return Outbox 목록
     */
    public List<LegacyOrderConversionOutbox> findProcessingTimeoutOutboxes(
            Instant timeoutThreshold, int limit) {
        return queryPort.findProcessingTimeoutOutboxes(timeoutThreshold, limit);
    }
}
