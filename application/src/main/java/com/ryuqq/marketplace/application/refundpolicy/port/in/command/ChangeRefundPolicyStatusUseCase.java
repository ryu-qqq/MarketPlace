package com.ryuqq.marketplace.application.refundpolicy.port.in.command;

import com.ryuqq.marketplace.application.refundpolicy.dto.command.ChangeRefundPolicyStatusCommand;

/** 환불정책 상태 변경 UseCase. */
public interface ChangeRefundPolicyStatusUseCase {

    void execute(ChangeRefundPolicyStatusCommand command);
}
