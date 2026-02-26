package com.ryuqq.marketplace.application.shippingpolicy.port.in.command;

import com.ryuqq.marketplace.application.shippingpolicy.dto.command.UpdateShippingPolicyCommand;

/** 배송정책 수정 UseCase. */
public interface UpdateShippingPolicyUseCase {

    void execute(UpdateShippingPolicyCommand command);
}
