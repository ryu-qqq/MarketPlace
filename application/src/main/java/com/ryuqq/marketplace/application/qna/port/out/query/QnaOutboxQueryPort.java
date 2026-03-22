package com.ryuqq.marketplace.application.qna.port.out.query;

import com.ryuqq.marketplace.domain.qna.outbox.aggregate.QnaOutbox;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/** QnA 아웃박스 조회 포트. */
public interface QnaOutboxQueryPort {
    Optional<QnaOutbox> findById(long id);
    List<QnaOutbox> findPendingOutboxes(Instant beforeTime, int limit);
    List<QnaOutbox> findProcessingTimeoutOutboxes(Instant timeoutBefore, int limit);
}
