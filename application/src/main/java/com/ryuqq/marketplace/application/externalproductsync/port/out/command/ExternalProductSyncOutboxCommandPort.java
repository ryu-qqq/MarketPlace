package com.ryuqq.marketplace.application.externalproductsync.port.out.command;

import com.ryuqq.marketplace.domain.externalproductsync.aggregate.ExternalProductSyncOutbox;
import java.util.List;

/** 외부 상품 연동 Outbox 커맨드 포트. */
public interface ExternalProductSyncOutboxCommandPort {

    /**
     * Outbox 단건 저장.
     *
     * @param outbox 저장할 Outbox
     * @return 저장된 엔티티 ID
     */
    Long persist(ExternalProductSyncOutbox outbox);

    /**
     * Outbox 배치 저장.
     *
     * @param outboxes 저장할 Outbox 목록
     */
    void persistAll(List<ExternalProductSyncOutbox> outboxes);
}
