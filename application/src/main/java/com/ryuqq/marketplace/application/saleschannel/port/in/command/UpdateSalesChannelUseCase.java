package com.ryuqq.marketplace.application.saleschannel.port.in.command;

import com.ryuqq.marketplace.application.saleschannel.dto.command.UpdateSalesChannelCommand;

/** 판매채널 수정 UseCase. */
public interface UpdateSalesChannelUseCase {
    void execute(UpdateSalesChannelCommand command);
}
