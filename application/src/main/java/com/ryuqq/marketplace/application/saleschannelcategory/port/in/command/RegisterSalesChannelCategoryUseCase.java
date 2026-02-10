package com.ryuqq.marketplace.application.saleschannelcategory.port.in.command;

import com.ryuqq.marketplace.application.saleschannelcategory.dto.command.RegisterSalesChannelCategoryCommand;

/** 외부 채널 카테고리 등록 UseCase. */
public interface RegisterSalesChannelCategoryUseCase {
    Long execute(RegisterSalesChannelCategoryCommand command);
}
