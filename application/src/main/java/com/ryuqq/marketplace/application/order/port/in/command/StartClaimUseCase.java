package com.ryuqq.marketplace.application.order.port.in.command;

import com.ryuqq.marketplace.application.order.dto.command.StartClaimCommand;

/** 클레임 시작(반품 요청) UseCase. */
public interface StartClaimUseCase {

    void execute(StartClaimCommand command);
}
