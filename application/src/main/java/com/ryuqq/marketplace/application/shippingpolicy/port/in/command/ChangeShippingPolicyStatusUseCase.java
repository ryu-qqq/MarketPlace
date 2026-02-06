package com.ryuqq.marketplace.application.shippingpolicy.port.in.command;

import com.ryuqq.marketplace.application.shippingpolicy.dto.command.ChangeShippingPolicyStatusCommand;

/** 배송정책 상태 변경 UseCase. */
public interface ChangeShippingPolicyStatusUseCase {

    void execute(ChangeShippingPolicyStatusCommand command);
}
