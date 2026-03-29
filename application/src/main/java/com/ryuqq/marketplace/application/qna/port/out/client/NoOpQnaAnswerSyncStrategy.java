package com.ryuqq.marketplace.application.qna.port.out.client;

import com.ryuqq.marketplace.application.common.dto.result.OutboxSyncResult;
import com.ryuqq.marketplace.domain.qna.outbox.aggregate.QnaOutbox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

/** 외부 QnA 동기화 전략이 없을 때 사용하는 No-op 구현체. */
@Component
@ConditionalOnMissingBean(
        value = QnaAnswerSyncStrategy.class,
        ignored = NoOpQnaAnswerSyncStrategy.class)
public class NoOpQnaAnswerSyncStrategy implements QnaAnswerSyncStrategy {

    private static final Logger log = LoggerFactory.getLogger(NoOpQnaAnswerSyncStrategy.class);

    @Override
    public OutboxSyncResult execute(QnaOutbox outbox) {
        log.warn("QnA 외부 동기화 전략 미등록, 아웃박스 건너뜀: outboxId={}", outbox.idValue());
        return OutboxSyncResult.success();
    }
}
