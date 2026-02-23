package com.ryuqq.marketplace.application.outboundsync.port.out.command;

import com.ryuqq.marketplace.domain.outboundsync.aggregate.OutboundSyncOutbox;
import java.util.List;

/** 외부 상품 연동 Outbox 커맨드 포트. */
public interface OutboundSyncOutboxCommandPort {

    /**
     * Outbox 단건 저장.
     *
     * @param outbox 저장할 Outbox
     * @return 저장된 엔티티 ID
     */
    Long persist(OutboundSyncOutbox outbox);

    /**
     * Outbox 배치 저장.
     *
     * @param outboxes 저장할 Outbox 목록
     */
    void persistAll(List<OutboundSyncOutbox> outboxes);
}
