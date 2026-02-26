package com.ryuqq.marketplace.application.legacyconversion.port.out.command;

import com.ryuqq.marketplace.domain.legacyconversion.aggregate.LegacyConversionOutbox;

/** 레거시 변환 Outbox 명령 포트. */
public interface LegacyConversionOutboxCommandPort {

    /**
     * LegacyConversionOutbox 영속화 (생성/수정).
     *
     * @param outbox 영속화할 Outbox
     * @return 영속화된 Outbox ID
     */
    Long persist(LegacyConversionOutbox outbox);
}
