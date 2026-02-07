package com.ryuqq.marketplace.application.refundpolicy.port.in.command;

import com.ryuqq.marketplace.application.refundpolicy.dto.command.RegisterRefundPolicyCommand;

/** 환불정책 등록 UseCase. */
public interface RegisterRefundPolicyUseCase {

    Long execute(RegisterRefundPolicyCommand command);
}
