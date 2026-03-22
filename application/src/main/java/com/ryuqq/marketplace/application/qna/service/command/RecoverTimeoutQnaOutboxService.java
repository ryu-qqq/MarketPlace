package com.ryuqq.marketplace.application.qna.service.command;

import com.ryuqq.marketplace.application.qna.dto.command.RecoverTimeoutQnaOutboxCommand;
import com.ryuqq.marketplace.application.qna.manager.QnaOutboxCommandManager;
import com.ryuqq.marketplace.application.qna.manager.QnaOutboxReadManager;
import com.ryuqq.marketplace.application.qna.port.in.command.RecoverTimeoutQnaOutboxUseCase;
import com.ryuqq.marketplace.domain.qna.outbox.aggregate.QnaOutbox;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class RecoverTimeoutQnaOutboxService implements RecoverTimeoutQnaOutboxUseCase {

    private static final Logger log = LoggerFactory.getLogger(RecoverTimeoutQnaOutboxService.class);

    private final QnaOutboxReadManager readManager;
    private final QnaOutboxCommandManager commandManager;

    public RecoverTimeoutQnaOutboxService(
            QnaOutboxReadManager readManager, QnaOutboxCommandManager commandManager) {
        this.readManager = readManager;
        this.commandManager = commandManager;
    }

    @Override
    public void execute(RecoverTimeoutQnaOutboxCommand command) {
        Instant timeoutBefore = Instant.now().minusSeconds(command.timeoutSeconds());
        List<QnaOutbox> timeoutOutboxes =
                readManager.findProcessingTimeoutOutboxes(timeoutBefore, command.batchSize());

        if (timeoutOutboxes.isEmpty()) {
            return;
        }

        log.info("QnA 아웃박스 타임아웃 복구 시작: {}건", timeoutOutboxes.size());
        Instant now = Instant.now();
        for (QnaOutbox outbox : timeoutOutboxes) {
            outbox.recoverFromTimeout(now);
            commandManager.persist(outbox);
        }
    }
}
