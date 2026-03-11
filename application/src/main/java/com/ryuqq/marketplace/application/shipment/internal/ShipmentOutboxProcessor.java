package com.ryuqq.marketplace.application.shipment.internal;

import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.shipment.manager.ShipmentOutboxCommandManager;
import com.ryuqq.marketplace.application.shipment.manager.ShipmentOutboxReadManager;
import com.ryuqq.marketplace.domain.shipment.outbox.aggregate.ShipmentOutbox;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 배송 아웃박스 처리기.
 *
 * <p>PENDING 상태의 아웃박스를 처리하여 외부 채널에 배송 상태를 동기화합니다.
 *
 * <p>처리 흐름:
 *
 * <ol>
 *   <li>startProcessing → PROCESSING 상태로 전이 + 저장
 *   <li>외부 API 호출 (Strategy 패턴 - 향후 구현)
 *   <li>성공 시 complete(), 실패 시 recordFailure()
 * </ol>
 *
 * <p>낙관적 락 충돌 시 re-read 패턴으로 최신 버전을 조회한 후 상태를 갱신합니다.
 */
@Component
public class ShipmentOutboxProcessor {

    private static final Logger log = LoggerFactory.getLogger(ShipmentOutboxProcessor.class);

    private final ShipmentOutboxCommandManager outboxCommandManager;
    private final ShipmentOutboxReadManager outboxReadManager;
    private final TimeProvider timeProvider;

    public ShipmentOutboxProcessor(
            ShipmentOutboxCommandManager outboxCommandManager,
            ShipmentOutboxReadManager outboxReadManager,
            TimeProvider timeProvider) {
        this.outboxCommandManager = outboxCommandManager;
        this.outboxReadManager = outboxReadManager;
        this.timeProvider = timeProvider;
    }

    /**
     * 아웃박스 단건 처리.
     *
     * @param outbox 처리할 아웃박스
     * @return 성공 여부
     */
    public boolean processOutbox(ShipmentOutbox outbox) {
        Instant now = timeProvider.now();
        Long outboxId = outbox.idValue();

        try {
            outbox.startProcessing(now);
            outboxCommandManager.persist(outbox);

            // TODO: 외부 채널 Strategy 라우팅 + API 호출
            // ShipmentOutboundStrategy strategy = strategyRouter.route(outbox);
            // ShipmentOutboundResult result = strategy.execute(outbox);
            //
            // if (result.success()) {
            //     outbox.complete(timeProvider.now());
            //     outboxCommandManager.persist(outbox);
            // } else {
            //     outbox.recordFailure(result.retryable(), result.errorMessage(),
            // timeProvider.now());
            //     outboxCommandManager.persist(outbox);
            // }

            // 임시: Strategy 미구현 → 바로 COMPLETED 처리
            outbox.complete(timeProvider.now());
            outboxCommandManager.persist(outbox);

            return true;
        } catch (Exception e) {
            log.error(
                    "배송 Outbox 처리 실패: outboxId={}, orderItemId={}, type={}, error={}",
                    outboxId,
                    outbox.orderItemIdValue(),
                    outbox.outboxType(),
                    e.getMessage(),
                    e);
            persistFailureWithReRead(outboxId, true, e.getMessage());
            return false;
        }
    }

    private void persistFailureWithReRead(Long outboxId, boolean retryable, String errorMessage) {
        try {
            ShipmentOutbox freshOutbox = outboxReadManager.getById(outboxId);
            freshOutbox.recordFailure(retryable, errorMessage, timeProvider.now());
            outboxCommandManager.persist(freshOutbox);
        } catch (Exception reReadEx) {
            log.warn(
                    "배송 Outbox re-read 실패, 상태 변경 건너뜀: outboxId={}, error={}",
                    outboxId,
                    reReadEx.getMessage());
        }
    }
}
