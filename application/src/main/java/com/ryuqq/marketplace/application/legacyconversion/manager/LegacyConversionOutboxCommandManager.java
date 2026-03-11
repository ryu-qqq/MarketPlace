package com.ryuqq.marketplace.application.legacyconversion.manager;

import com.ryuqq.marketplace.application.legacyconversion.port.out.command.LegacyConversionOutboxCommandPort;
import com.ryuqq.marketplace.application.legacyconversion.port.out.query.LegacyConversionOutboxQueryPort;
import com.ryuqq.marketplace.domain.legacyconversion.aggregate.LegacyConversionOutbox;
import java.time.Instant;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/** 레거시 변환 Outbox 명령 Manager. */
@Component
public class LegacyConversionOutboxCommandManager {

    private final LegacyConversionOutboxCommandPort commandPort;
    private final LegacyConversionOutboxQueryPort queryPort;

    public LegacyConversionOutboxCommandManager(
            LegacyConversionOutboxCommandPort commandPort,
            LegacyConversionOutboxQueryPort queryPort) {
        this.commandPort = commandPort;
        this.queryPort = queryPort;
    }

    /**
     * Outbox 영속화.
     *
     * @param outbox 영속화할 Outbox
     * @return 영속화된 Outbox ID
     */
    public Long persist(LegacyConversionOutbox outbox) {
        return commandPort.persist(outbox);
    }

    /**
     * 별도 트랜잭션으로 Outbox 실패 처리.
     *
     * <p>외부 트랜잭션이 rollback-only로 마킹되어도 독립적으로 커밋됩니다.
     *
     * @param outbox 실패 처리할 Outbox
     * @param errorMessage 에러 메시지
     * @param now 현재 시각
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void failInNewTransaction(
            LegacyConversionOutbox outbox, String errorMessage, Instant now) {
        outbox.failAndRetry(errorMessage, now);
        commandPort.persist(outbox);
    }

    /**
     * PENDING 상태 Outbox가 없을 때만 새 Outbox를 생성합니다.
     *
     * <p>이미 PENDING 상태가 존재하면, 스케줄러가 최신 데이터를 읽어 처리하므로 중복 생성하지 않습니다.
     *
     * @param legacyProductGroupId 레거시 상품그룹 ID
     * @param now 현재 시각
     */
    public void createIfNoPending(long legacyProductGroupId, Instant now) {
        if (queryPort.existsPendingByLegacyProductGroupId(legacyProductGroupId)) {
            return;
        }
        LegacyConversionOutbox outbox = LegacyConversionOutbox.forNew(legacyProductGroupId, now);
        commandPort.persist(outbox);
    }
}
