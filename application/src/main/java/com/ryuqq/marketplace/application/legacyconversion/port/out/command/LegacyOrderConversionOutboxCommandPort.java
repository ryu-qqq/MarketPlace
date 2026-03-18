package com.ryuqq.marketplace.application.legacyconversion.port.out.command;

import com.ryuqq.marketplace.domain.legacyconversion.aggregate.LegacyOrderConversionOutbox;
import java.util.List;

/** 레거시 주문 변환 Outbox 명령 포트. */
public interface LegacyOrderConversionOutboxCommandPort {

    /**
     * LegacyOrderConversionOutbox 영속화 (생성/수정).
     *
     * @param outbox 영속화할 Outbox
     * @return 영속화된 Outbox ID
     */
    Long persist(LegacyOrderConversionOutbox outbox);

    /**
     * LegacyOrderConversionOutbox 목록 일괄 영속화.
     *
     * @param outboxes 영속화할 Outbox 목록
     * @return 영속화된 Outbox ID 목록
     */
    List<Long> persistAll(List<LegacyOrderConversionOutbox> outboxes);
}
