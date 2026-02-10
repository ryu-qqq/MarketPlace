package com.ryuqq.marketplace.application.saleschannelbrand.port.in.command;

import com.ryuqq.marketplace.application.saleschannelbrand.dto.command.RegisterSalesChannelBrandCommand;

/** 외부채널 브랜드 등록 UseCase. */
public interface RegisterSalesChannelBrandUseCase {
    Long execute(RegisterSalesChannelBrandCommand command);
}
