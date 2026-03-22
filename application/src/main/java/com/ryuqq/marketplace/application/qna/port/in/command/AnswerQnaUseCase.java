package com.ryuqq.marketplace.application.qna.port.in.command;

import com.ryuqq.marketplace.application.qna.dto.command.AnswerQnaCommand;
import com.ryuqq.marketplace.application.qna.dto.result.QnaReplyResult;

/** QnA 답변 등록 UseCase. */
public interface AnswerQnaUseCase {
    QnaReplyResult execute(AnswerQnaCommand command);
}
