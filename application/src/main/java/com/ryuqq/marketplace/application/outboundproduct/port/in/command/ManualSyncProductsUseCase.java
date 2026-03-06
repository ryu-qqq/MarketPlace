package com.ryuqq.marketplace.application.outboundproduct.port.in.command;

import com.ryuqq.marketplace.application.outboundproduct.dto.command.ManualSyncProductsCommand;
import com.ryuqq.marketplace.application.outboundproduct.dto.result.ManualSyncResult;

/** 수동 상품 외부몰 전송 UseCase. */
public interface ManualSyncProductsUseCase {
    ManualSyncResult execute(ManualSyncProductsCommand command);
}
