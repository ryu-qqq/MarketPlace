package com.ryuqq.marketplace.application.qna.port.in.command;

import com.ryuqq.marketplace.application.qna.dto.command.UpdateQnaReplyCommand;
import com.ryuqq.marketplace.application.qna.dto.result.QnaReplyResult;

/** QnA 답변 수정 UseCase. */
public interface UpdateQnaReplyUseCase {
    QnaReplyResult execute(UpdateQnaReplyCommand command);
}
