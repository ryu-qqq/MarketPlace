package com.ryuqq.marketplace.application.qna.service.command;

import com.ryuqq.marketplace.application.qna.dto.command.CloseQnaCommand;
import com.ryuqq.marketplace.application.qna.manager.QnaCommandManager;
import com.ryuqq.marketplace.application.qna.manager.QnaReadManager;
import com.ryuqq.marketplace.application.qna.port.in.command.CloseQnaUseCase;
import com.ryuqq.marketplace.domain.qna.aggregate.Qna;
import java.time.Instant;
import org.springframework.stereotype.Service;

@Service
public class CloseQnaService implements CloseQnaUseCase {

    private final QnaReadManager readManager;
    private final QnaCommandManager commandManager;

    public CloseQnaService(QnaReadManager readManager, QnaCommandManager commandManager) {
        this.readManager = readManager;
        this.commandManager = commandManager;
    }

    @Override
    public void execute(CloseQnaCommand command) {
        Qna qna = readManager.getById(command.qnaId());
        qna.close(Instant.now());
        commandManager.persist(qna);
    }
}
