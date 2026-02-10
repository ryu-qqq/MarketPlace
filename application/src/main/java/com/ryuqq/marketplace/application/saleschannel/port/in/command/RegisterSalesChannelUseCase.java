package com.ryuqq.marketplace.application.saleschannel.port.in.command;

import com.ryuqq.marketplace.application.saleschannel.dto.command.RegisterSalesChannelCommand;

/** 판매채널 등록 UseCase. */
public interface RegisterSalesChannelUseCase {
    Long execute(RegisterSalesChannelCommand command);
}
