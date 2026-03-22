package com.ryuqq.marketplace.application.qna.service.command;

import com.ryuqq.marketplace.application.qna.dto.command.ProcessPendingQnaOutboxCommand;
import com.ryuqq.marketplace.application.qna.internal.QnaOutboxRelayProcessor;
import com.ryuqq.marketplace.application.qna.manager.QnaOutboxReadManager;
import com.ryuqq.marketplace.application.qna.port.in.command.ProcessPendingQnaOutboxUseCase;
import com.ryuqq.marketplace.domain.qna.outbox.aggregate.QnaOutbox;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ProcessPendingQnaOutboxService implements ProcessPendingQnaOutboxUseCase {

    private static final Logger log = LoggerFactory.getLogger(ProcessPendingQnaOutboxService.class);

    private final QnaOutboxReadManager readManager;
    private final QnaOutboxRelayProcessor relayProcessor;

    public ProcessPendingQnaOutboxService(
            QnaOutboxReadManager readManager, QnaOutboxRelayProcessor relayProcessor) {
        this.readManager = readManager;
        this.relayProcessor = relayProcessor;
    }

    @Override
    public void execute(ProcessPendingQnaOutboxCommand command) {
        Instant beforeTime = Instant.now().minusSeconds(command.delaySeconds());
        List<QnaOutbox> pendingOutboxes =
                readManager.findPendingOutboxes(beforeTime, command.batchSize());

        if (pendingOutboxes.isEmpty()) {
            return;
        }

        log.info("QnA 아웃박스 처리 시작: {}건", pendingOutboxes.size());
        for (QnaOutbox outbox : pendingOutboxes) {
            relayProcessor.relay(outbox);
        }
    }
}
