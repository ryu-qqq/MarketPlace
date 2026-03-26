package com.ryuqq.marketplace.application.qna.port.in.command;

import com.ryuqq.marketplace.application.qna.dto.command.RecoverTimeoutQnaOutboxCommand;

/** PROCESSING 타임아웃 QnA 아웃박스 복구 UseCase. */
public interface RecoverTimeoutQnaOutboxUseCase {
    void execute(RecoverTimeoutQnaOutboxCommand command);
}
