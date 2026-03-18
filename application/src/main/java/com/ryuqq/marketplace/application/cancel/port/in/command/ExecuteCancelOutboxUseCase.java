package com.ryuqq.marketplace.application.cancel.port.in.command;

import com.ryuqq.marketplace.application.cancel.dto.command.ExecuteCancelOutboxCommand;

/**
 * 취소 Outbox 실행 유스케이스 포트.
 *
 * <p>SQS Consumer에서 수신한 취소 Outbox를 처리합니다.
 */
public interface ExecuteCancelOutboxUseCase {

    void execute(ExecuteCancelOutboxCommand command);
}
