package com.ryuqq.marketplace.application.refundpolicy.port.in.command;

import com.ryuqq.marketplace.application.refundpolicy.dto.command.UpdateRefundPolicyCommand;

/** 환불정책 수정 UseCase. */
public interface UpdateRefundPolicyUseCase {

    void execute(UpdateRefundPolicyCommand command);
}
