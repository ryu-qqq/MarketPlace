package com.ryuqq.marketplace.application.qna.manager;

import com.ryuqq.marketplace.application.qna.port.out.command.QnaOutboxCommandPort;
import com.ryuqq.marketplace.domain.qna.outbox.aggregate.QnaOutbox;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class QnaOutboxCommandManager {

    private final QnaOutboxCommandPort commandPort;

    public QnaOutboxCommandManager(QnaOutboxCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    @Transactional
    public void persist(QnaOutbox outbox) {
        commandPort.persist(outbox);
    }
}
