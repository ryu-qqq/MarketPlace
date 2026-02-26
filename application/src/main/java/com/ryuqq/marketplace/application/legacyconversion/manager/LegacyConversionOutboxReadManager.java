package com.ryuqq.marketplace.application.legacyconversion.manager;

import com.ryuqq.marketplace.application.legacyconversion.port.out.query.LegacyConversionOutboxQueryPort;
import com.ryuqq.marketplace.domain.legacyconversion.aggregate.LegacyConversionOutbox;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Component;

/** 레거시 변환 Outbox 조회 Manager. */
@Component
public class LegacyConversionOutboxReadManager {

    private final LegacyConversionOutboxQueryPort queryPort;

    public LegacyConversionOutboxReadManager(LegacyConversionOutboxQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    /**
     * 처리 대기 중인 Outbox 목록 조회.
     *
     * @param beforeTime 이 시간 이전에 생성된 것만 조회
     * @param limit 최대 조회 개수
     * @return Outbox 목록
     */
    public List<LegacyConversionOutbox> findPendingOutboxes(Instant beforeTime, int limit) {
        return queryPort.findPendingOutboxes(beforeTime, limit);
    }

    /**
     * PROCESSING 타임아웃 Outbox 목록 조회.
     *
     * @param timeoutThreshold 이 시간 이전에 업데이트된 것만 조회
     * @param limit 최대 조회 개수
     * @return Outbox 목록
     */
    public List<LegacyConversionOutbox> findProcessingTimeoutOutboxes(
            Instant timeoutThreshold, int limit) {
        return queryPort.findProcessingTimeoutOutboxes(timeoutThreshold, limit);
    }

    /**
     * 해당 legacyProductGroupId에 PENDING 상태 Outbox가 존재하는지 확인.
     *
     * @param legacyProductGroupId 레거시 상품그룹 ID
     * @return PENDING 존재 여부
     */
    public boolean existsPendingByLegacyProductGroupId(long legacyProductGroupId) {
        return queryPort.existsPendingByLegacyProductGroupId(legacyProductGroupId);
    }
}
