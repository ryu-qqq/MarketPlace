package com.ryuqq.marketplace.application.qna.service.command;

import com.ryuqq.marketplace.application.common.dto.result.OutboxSyncResult;
import com.ryuqq.marketplace.application.common.exception.ExternalServiceUnavailableException;
import com.ryuqq.marketplace.application.qna.dto.command.ExecuteQnaOutboxCommand;
import com.ryuqq.marketplace.application.qna.manager.QnaOutboxCommandManager;
import com.ryuqq.marketplace.application.qna.manager.QnaOutboxReadManager;
import com.ryuqq.marketplace.application.qna.port.in.command.ExecuteQnaOutboxUseCase;
import com.ryuqq.marketplace.application.qna.port.out.client.QnaAnswerSyncStrategy;
import com.ryuqq.marketplace.domain.qna.outbox.aggregate.QnaOutbox;
import java.time.Instant;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/** QnA 아웃박스 실행 서비스. SQS Consumer에서 호출하여 실제 외부 API를 호출합니다. */
@Service
public class ExecuteQnaOutboxService implements ExecuteQnaOutboxUseCase {

    private static final Logger log = LoggerFactory.getLogger(ExecuteQnaOutboxService.class);

    private final QnaOutboxReadManager readManager;
    private final QnaOutboxCommandManager commandManager;
    private final QnaAnswerSyncStrategy syncStrategy;

    public ExecuteQnaOutboxService(
            QnaOutboxReadManager readManager,
            QnaOutboxCommandManager commandManager,
            Optional<QnaAnswerSyncStrategy> syncStrategy) {
        this.readManager = readManager;
        this.commandManager = commandManager;
        this.syncStrategy = syncStrategy.orElse(null);
    }

    @Override
    public void execute(ExecuteQnaOutboxCommand command) {
        if (syncStrategy == null) {
            log.warn("QnaAnswerSyncStrategy 미등록, 아웃박스 건너뜀: outboxId={}", command.outboxId());
            return;
        }

        QnaOutbox outbox = readManager.getById(command.outboxId());

        try {
            OutboxSyncResult result = syncStrategy.execute(outbox);

            QnaOutbox freshOutbox = readManager.getById(command.outboxId());
            Instant now = Instant.now();

            if (result.isSuccess()) {
                freshOutbox.complete(now);
                log.info(
                        "QnA 아웃박스 동기화 성공: outboxId={}, qnaId={}",
                        command.outboxId(),
                        command.qnaId());
            } else {
                freshOutbox.recordFailure(result.retryable(), result.errorMessage(), now);
                log.warn(
                        "QnA 아웃박스 동기화 실패: outboxId={}, retryable={}, error={}",
                        command.outboxId(),
                        result.retryable(),
                        result.errorMessage());
            }
            commandManager.persist(freshOutbox);

        } catch (ExternalServiceUnavailableException e) {
            log.error("외부 서비스 불가: outboxId={}", command.outboxId(), e);
            QnaOutbox freshOutbox = readManager.getById(command.outboxId());
            freshOutbox.recoverFromTimeout(Instant.now());
            commandManager.persist(freshOutbox);
        } catch (Exception e) {
            log.error("QnA 아웃박스 실행 중 예외: outboxId={}", command.outboxId(), e);
            QnaOutbox freshOutbox = readManager.getById(command.outboxId());
            freshOutbox.recordFailure(true, e.getMessage(), Instant.now());
            commandManager.persist(freshOutbox);
        }
    }
}
