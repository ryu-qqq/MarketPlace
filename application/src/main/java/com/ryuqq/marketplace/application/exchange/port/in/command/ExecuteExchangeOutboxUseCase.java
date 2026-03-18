package com.ryuqq.marketplace.application.exchange.port.in.command;

import com.ryuqq.marketplace.application.exchange.dto.command.ExecuteExchangeOutboxCommand;

/**
 * 교환 Outbox 실행 유스케이스 포트.
 *
 * <p>SQS Consumer에서 수신한 교환 Outbox를 처리합니다.
 */
public interface ExecuteExchangeOutboxUseCase {

    void execute(ExecuteExchangeOutboxCommand command);
}
