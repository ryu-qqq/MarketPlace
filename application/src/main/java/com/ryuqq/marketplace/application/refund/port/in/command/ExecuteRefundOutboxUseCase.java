package com.ryuqq.marketplace.application.refund.port.in.command;

import com.ryuqq.marketplace.application.refund.dto.command.ExecuteRefundOutboxCommand;

/**
 * 환불 Outbox 실행 유스케이스 포트.
 *
 * <p>SQS Consumer에서 수신한 환불 Outbox를 처리합니다.
 */
public interface ExecuteRefundOutboxUseCase {

    void execute(ExecuteRefundOutboxCommand command);
}
