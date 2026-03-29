package com.ryuqq.marketplace.application.qna.port.out.client;

import com.ryuqq.marketplace.application.common.dto.result.OutboxSyncResult;
import com.ryuqq.marketplace.domain.qna.outbox.aggregate.QnaOutbox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 외부 QnA 동기화 전략이 없을 때 사용하는 No-op 구현체.
 *
 * <p>NaverQnaAnswerSyncStrategy 등 실제 구현체가 있으면 Spring의 빈 선택에서 밀립니다. 실제 구현체가
 * {@code @ConditionalOnBean}으로 조건부 등록되므로, 해당 빈이 없는 환경에서 이 No-op이 주입됩니다.
 */
@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class NoOpQnaAnswerSyncStrategy implements QnaAnswerSyncStrategy {

    private static final Logger log = LoggerFactory.getLogger(NoOpQnaAnswerSyncStrategy.class);

    @Override
    public OutboxSyncResult execute(QnaOutbox outbox) {
        log.warn("QnA 외부 동기화 전략 미등록, 아웃박스 건너뜀: outboxId={}", outbox.idValue());
        return OutboxSyncResult.success();
    }
}
