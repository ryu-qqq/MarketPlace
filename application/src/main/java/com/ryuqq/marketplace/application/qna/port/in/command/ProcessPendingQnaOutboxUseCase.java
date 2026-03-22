package com.ryuqq.marketplace.application.qna.port.in.command;

import com.ryuqq.marketplace.application.qna.dto.command.ProcessPendingQnaOutboxCommand;

/** PENDING 상태 QnA 아웃박스 일괄 처리 UseCase. */
public interface ProcessPendingQnaOutboxUseCase {
    void execute(ProcessPendingQnaOutboxCommand command);
}
