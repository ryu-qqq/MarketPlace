package com.ryuqq.marketplace.application.shippingpolicy.port.in.command;

import com.ryuqq.marketplace.application.shippingpolicy.dto.command.RegisterShippingPolicyCommand;

/** 배송정책 등록 UseCase. */
public interface RegisterShippingPolicyUseCase {

    Long execute(RegisterShippingPolicyCommand command);
}
