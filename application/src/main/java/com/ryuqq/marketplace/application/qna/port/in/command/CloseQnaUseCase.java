package com.ryuqq.marketplace.application.qna.port.in.command;

import com.ryuqq.marketplace.application.qna.dto.command.CloseQnaCommand;

/** QnA 종결 UseCase. */
public interface CloseQnaUseCase {
    void execute(CloseQnaCommand command);
}
