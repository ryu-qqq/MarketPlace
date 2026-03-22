package com.ryuqq.marketplace.application.qna.service.command;

import com.ryuqq.marketplace.application.qna.dto.command.AnswerQnaCommand;
import com.ryuqq.marketplace.application.qna.dto.result.QnaReplyResult;
import com.ryuqq.marketplace.application.qna.manager.QnaCommandManager;
import com.ryuqq.marketplace.application.qna.manager.QnaOutboxCommandManager;
import com.ryuqq.marketplace.application.qna.manager.QnaReadManager;
import com.ryuqq.marketplace.application.qna.port.in.command.AnswerQnaUseCase;
import com.ryuqq.marketplace.domain.common.event.DomainEvent;
import com.ryuqq.marketplace.domain.qna.aggregate.Qna;
import com.ryuqq.marketplace.domain.qna.aggregate.QnaReply;
import com.ryuqq.marketplace.domain.qna.outbox.aggregate.QnaOutbox;
import com.ryuqq.marketplace.domain.qna.outbox.vo.QnaOutboxType;
import java.time.Instant;
import java.util.List;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class AnswerQnaService implements AnswerQnaUseCase {

    private final QnaReadManager readManager;
    private final QnaCommandManager commandManager;
    private final QnaOutboxCommandManager outboxCommandManager;
    private final ApplicationEventPublisher eventPublisher;

    public AnswerQnaService(
            QnaReadManager readManager,
            QnaCommandManager commandManager,
            QnaOutboxCommandManager outboxCommandManager,
            ApplicationEventPublisher eventPublisher) {
        this.readManager = readManager;
        this.commandManager = commandManager;
        this.outboxCommandManager = outboxCommandManager;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public QnaReplyResult execute(AnswerQnaCommand command) {
        Instant now = Instant.now();
        Qna qna = readManager.getById(command.qnaId());
        QnaReply reply =
                qna.answer(command.content(), command.authorName(), command.parentReplyId(), now);
        commandManager.persist(qna);

        QnaOutbox outbox =
                QnaOutbox.forNew(
                        qna.id(),
                        qna.source().salesChannelId(),
                        qna.source().externalQnaId(),
                        QnaOutboxType.ANSWER,
                        command.content(),
                        now);
        outboxCommandManager.persist(outbox);

        List<DomainEvent> events = qna.pollEvents();
        events.forEach(eventPublisher::publishEvent);

        // persist 후 재조회하여 auto-generated ID를 포함한 최신 reply 반환
        Qna persisted = readManager.getById(command.qnaId());
        QnaReply savedReply =
                persisted.replies().stream()
                        .filter(
                                r ->
                                        r.content().equals(reply.content())
                                                && r.authorName().equals(reply.authorName()))
                        .reduce((first, second) -> second)
                        .orElse(reply);

        return new QnaReplyResult(
                savedReply.idValue(),
                savedReply.parentReplyId(),
                savedReply.content(),
                savedReply.authorName(),
                savedReply.replyType(),
                savedReply.createdAt());
    }
}
