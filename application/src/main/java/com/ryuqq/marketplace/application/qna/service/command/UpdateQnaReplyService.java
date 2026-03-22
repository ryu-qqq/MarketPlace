package com.ryuqq.marketplace.application.qna.service.command;

import com.ryuqq.marketplace.application.qna.dto.command.UpdateQnaReplyCommand;
import com.ryuqq.marketplace.application.qna.dto.result.QnaReplyResult;
import com.ryuqq.marketplace.application.qna.manager.QnaCommandManager;
import com.ryuqq.marketplace.application.qna.manager.QnaReadManager;
import com.ryuqq.marketplace.application.qna.port.in.command.UpdateQnaReplyUseCase;
import com.ryuqq.marketplace.domain.qna.aggregate.Qna;
import com.ryuqq.marketplace.domain.qna.aggregate.QnaReply;
import java.time.Instant;
import org.springframework.stereotype.Service;

@Service
public class UpdateQnaReplyService implements UpdateQnaReplyUseCase {

    private final QnaReadManager readManager;
    private final QnaCommandManager commandManager;

    public UpdateQnaReplyService(QnaReadManager readManager, QnaCommandManager commandManager) {
        this.readManager = readManager;
        this.commandManager = commandManager;
    }

    @Override
    public QnaReplyResult execute(UpdateQnaReplyCommand command) {
        Instant now = Instant.now();
        Qna qna = readManager.getById(command.qnaId());
        QnaReply reply = qna.updateReply(command.replyId(), command.content(), now);
        commandManager.persist(qna);

        return new QnaReplyResult(
                reply.idValue(),
                reply.parentReplyId(),
                reply.content(),
                reply.authorName(),
                reply.replyType(),
                reply.createdAt());
    }
}
