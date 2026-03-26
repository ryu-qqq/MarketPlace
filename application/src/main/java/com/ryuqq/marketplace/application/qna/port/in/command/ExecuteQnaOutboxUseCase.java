package com.ryuqq.marketplace.application.qna.port.in.command;

import com.ryuqq.marketplace.application.qna.dto.command.ExecuteQnaOutboxCommand;

/** QnA 아웃박스 실행 UseCase (SQS Consumer). */
public interface ExecuteQnaOutboxUseCase {
    void execute(ExecuteQnaOutboxCommand command);
}
