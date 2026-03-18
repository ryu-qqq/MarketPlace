package com.ryuqq.marketplace.application.legacyconversion.manager;

import com.ryuqq.marketplace.application.legacyconversion.port.out.command.LegacyOrderConversionOutboxCommandPort;
import com.ryuqq.marketplace.domain.legacyconversion.aggregate.LegacyOrderConversionOutbox;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/** 레거시 주문 변환 Outbox 명령 Manager. */
@Component
public class LegacyOrderConversionOutboxCommandManager {

    private final LegacyOrderConversionOutboxCommandPort commandPort;

    public LegacyOrderConversionOutboxCommandManager(
            LegacyOrderConversionOutboxCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    /**
     * Outbox 영속화.
     *
     * @param outbox 영속화할 Outbox
     * @return 영속화된 Outbox ID
     */
    @Transactional
    public Long persist(LegacyOrderConversionOutbox outbox) {
        return commandPort.persist(outbox);
    }

    /**
     * Outbox 목록 일괄 영속화.
     *
     * @param outboxes 영속화할 Outbox 목록
     * @return 영속화된 Outbox ID 목록
     */
    @Transactional
    public List<Long> persistAll(List<LegacyOrderConversionOutbox> outboxes) {
        return commandPort.persistAll(outboxes);
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
            LegacyOrderConversionOutbox outbox, String errorMessage, Instant now) {
        outbox.failAndRetry(errorMessage, now);
        commandPort.persist(outbox);
    }
}
