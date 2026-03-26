package com.ryuqq.marketplace.application.qna.port.out.client;

import com.ryuqq.marketplace.application.common.dto.result.OutboxSyncResult;
import com.ryuqq.marketplace.domain.qna.outbox.aggregate.QnaOutbox;

/** QnA 답변 외부 동기화 전략 인터페이스. */
public interface QnaAnswerSyncStrategy {
    OutboxSyncResult execute(QnaOutbox outbox);
}
