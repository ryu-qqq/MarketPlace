package com.ryuqq.marketplace.application.qna.manager;

import com.ryuqq.marketplace.application.qna.port.out.command.QnaCommandPort;
import com.ryuqq.marketplace.domain.qna.aggregate.Qna;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class QnaCommandManager {

    private final QnaCommandPort commandPort;

    public QnaCommandManager(QnaCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    @Transactional
    public void persist(Qna qna) {
        commandPort.persist(qna);
    }
}
